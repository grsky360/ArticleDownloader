package ilio.adler.service;

import ilio.adler.service.data.Task;
import ilio.adler.service.data.Tuple2;
import ilio.adler.service.data.Tuple3;
import ilio.adler.util.CliTimer;
import ilio.adler.util.EpubPacker;
import ilio.adler.util.HttpClient;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zqy
 * @version 1.0
 * @date 2020/1/19 17:10
 */
public abstract class AbstractDownloader {
    private static final List<String> SKIP_IMAGE_URLS = Arrays.asList("static/image/", "bababian.com", "/js/", ".gif", "editor/images/face");

    protected boolean isNeedLogin() {
        return false;
    }

    protected void login() {
    }

    protected abstract Tuple2<String, Integer> parseTitleAndPageFromContent(String content);

    protected abstract Tuple2<String, List<String>> handleRawContentAndImages(String content);

    public boolean submit(Task task) {
        if (isNeedLogin()) {
            login();
        }

        Tuple3<String, String, List<String>> titleAndContentAndImages = downloadTitleAndContentAndImages(task);

        String title = titleAndContentAndImages._1;
        String[] lines = titleAndContentAndImages._2.split("\n");
        List<String> images = titleAndContentAndImages._3.stream().filter(image -> SKIP_IMAGE_URLS.stream().noneMatch(image::contains)).collect(Collectors.toList());

        for (int i = 0; i < lines.length; i++) {
            if (!images.contains(lines[i])) {
                lines[i] = lines[i].replaceAll("&lt;", "<").replaceAll("%gt;", ">");
            }
        }

        EpubPacker epubPacker = new EpubPacker(task.getPath(), task.getName(), title, lines, images);
        return epubPacker.pack();
    }

    private Tuple3<String, String, List<String>> downloadTitleAndContentAndImages(Task task) {
        String rawContent = downloadContentWithPage(task, 1);
        Tuple2<String, Integer> tuple = parseTitleAndPageFromContent(rawContent);

        Tuple2<String, List<String>> contentAndImages = handleRawContentAndImages(rawContent);

        StringBuilder content = new StringBuilder();
        content.append(tuple._1).append("\n")
            .append(generateUrlWithPage(task, 1)).append("\n")
            .append("Page ").append(1).append(" / ").append(tuple._2).append("\n")
            .append(contentAndImages._1).append("\n");
        List<String> images = new ArrayList<>(contentAndImages._2);

        for (int page = 2; page <= tuple._2; page++) {
            contentAndImages = handleRawContentAndImages(downloadContentWithPage(task, page, tuple._2));
            content.append(generateUrlWithPage(task, page)).append("\n")
                .append("Page ").append(page).append(" / ").append(tuple._2).append("\n")
                .append(contentAndImages._1).append("\n");
            images.addAll(contentAndImages._2);
        }
        return Tuple3.of(tuple._1, content.toString(), images);
    }

    private String downloadContentWithPage(Task task, int page) {
        return downloadContentWithPage(task, page, -1);
    }

    private String downloadContentWithPage(Task task, int page, int totalPage) {
        String url = generateUrlWithPage(task, page);
        String info = url + "\tPage: " + page + "/" + (totalPage == -1 ? "?" : totalPage);
        CliTimer timer = new CliTimer(info, "OK", 10, 2);

        Map<String, String> header = new HashMap<>();
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 6.3; rv:36.0) Gecko/20100101 Firefox/36.04");
        header.put("Content-Type", "text/html;charset=UTF-8");

        timer.start();
        try {
            String content = HttpClient.get(url, header);
            timer.stop();
            return content;
        } catch (Exception e) {
            timer.error();
        }
        return "";
    }

    private String generateUrlWithPage(Task task, int page) {
        String pageFlag = task.getArticleType().getPageFlag();
        String url = task.getUrl();
        int index = url.indexOf('?');
        if (index == -1) {
            return url + "?" + pageFlag + "=" + page;
        }
        if (index == url.length() - 1) {
            return url + pageFlag + "=" + page;
        }
        return url + "&" + pageFlag + "=" + page;
    }
}