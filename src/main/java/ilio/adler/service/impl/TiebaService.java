package ilio.adler.service.impl;

import ilio.adler.service.AbstractDownloader;
import ilio.adler.service.ArticleService;
import ilio.adler.service.data.Task;
import ilio.adler.service.data.Tuple2;
import ilio.adler.service.enums.ArticleTypeEnum;
import ilio.adler.util.CharCodeDecode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author zqy
 * @version 1.0
 * @date 2020/1/19 17:11
 */
@Component
public class TiebaService extends AbstractDownloader implements ArticleService {

    @Override
    public ArticleTypeEnum getArticleType() {
        return ArticleTypeEnum.Tieba;
    }

    @Override
    protected Tuple2<String, Integer> parseTitleAndPageFromContent(String content) {
        Document html = Jsoup.parse(content);
        Element titleElement = html.selectFirst(".core_title_txt");
        Element pageElement = html.selectFirst("#thread_theme_5 > div:nth-child(1) > ul:nth-child(1) > li:nth-child(2) > span:nth-child(2)");

        String title = Optional.ofNullable(titleElement).map(Element::text).orElse("");
        int page = Optional.ofNullable(pageElement).map(Element::text).map(Integer::parseInt).orElse(1);
        return Tuple2.of(title, page);
    }

    @Override
    protected Tuple2<String, List<String>> handleRawContentAndImages(String content) {
        Document html = Jsoup.parse(content);
        html.outputSettings(new Document.OutputSettings().prettyPrint(false));
        html.select("br").append("\n\n");
        html.select("p").append("\n\n");

        Elements elements = html.getElementsByClass("j_d_post_content");
        StringBuilder sb = new StringBuilder();
        List<String> images = new ArrayList<>();
        for (Element element : elements) {
            for (Element image : element.select("img")) {
                images.add(image.attr("src"));
            }
            sb.append(CharCodeDecode.replace(element.html()
                .replaceAll("<img.*?src=[\"'](.*?)[\"'].*?>", "$1")
            )).append("\n\n");
        }
        return Tuple2.of(sb.toString(), images);
    }
}
