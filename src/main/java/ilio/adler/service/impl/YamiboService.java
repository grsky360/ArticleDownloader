package ilio.adler.service.impl;

import ilio.adler.service.AbstractDownloader;
import ilio.adler.service.ArticleService;
import ilio.adler.service.data.Tuple2;
import ilio.adler.service.enums.ArticleTypeEnum;
import ilio.adler.util.CharCodeDecode;
import ilio.adler.util.HttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.util.*;

/**
 * @author zqy
 * @version 1.0
 * @date 2020/1/19 17:11
 */
@Component
public class YamiboService extends AbstractDownloader implements ArticleService {

    private static final String USERNAME = "parse";
    private static final String PASSWORD = "parse1231";
    private static final String HOST = "https://bbs.yamibo.com";
    private static final String LOGIN_URI = "/member.php?mod=logging&action=login&loginsubmit=yes&handlekey=login&loginhash=LUy46&inajax=1";
    private static final String FORM_URI = "/member.php?mod=logging&action=login";
    private static final String REFERER = "/forum.php";

    @Override
    public ArticleTypeEnum getArticleType() {
        return ArticleTypeEnum.Yamibo;
    }

    @Override
    protected boolean isNeedLogin() {
        return true;
    }

    @Override
    protected void login() {
        Map<String, String> data = new HashMap<>();
        data.put("loginfield", "username");
        data.put("username", USERNAME);
        data.put("password", DigestUtils.md5DigestAsHex(PASSWORD.getBytes()));
        data.put("referer", HOST + REFERER);
        data.put("questionid", "0");
        data.put("answer", "");
        try {
            data.put("formhash", Jsoup.parse(HttpClient.get(HOST + FORM_URI)).selectFirst("[name=formhash]").attr("value"));
            HttpClient.post(HOST + LOGIN_URI, data);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Tuple2<String, Integer> parseTitleAndPageFromContent(String content) {
        Document html = Jsoup.parse(content);
        Element titleElement = html.selectFirst("#thread_subject");
        Element pageElement = html.selectFirst(".pg");

        String title = Optional.ofNullable(titleElement).map(Element::text).orElse("");
        int page = Optional.ofNullable(pageElement).map(element -> {
            return Integer.parseInt(element.select("label span").attr("title").split(" ")[1]);
        }).orElse(1);
        return Tuple2.of(title, page);
    }

    @Override
    protected Tuple2<String, List<String>> handleRawContentAndImages(String content) {
        Document html = Jsoup.parse(content);
        html.outputSettings(new Document.OutputSettings().prettyPrint(false));

        Elements elements = html.getElementsByClass("t_f");
        StringBuilder sb = new StringBuilder();
        List<String> images = new ArrayList<>();
        for (Element element : elements) {
            for (Element img : element.select("img")) {
                String src = img.attr("src");
                if (src == null || "".equals(src)) {
                    src = img.attr("file");
                    if (src == null || "".equals(src)) {
                        continue;
                    }
                }
                if (!src.contains("http")) {
                    src = getArticleType().getHost() + "/" + src;
                }
                images.add(src);
            }
            sb.append(CharCodeDecode.replace(element.html()
                .replaceAll("<img.*?(src|file)=[\"'](.*?)[\"'].*?>", "$2")
            )).append("\n");
        }
        return Tuple2.of(sb.toString(), images);
    }

    public static void main(String[] args) {
        new YamiboService().login();
    }
}
