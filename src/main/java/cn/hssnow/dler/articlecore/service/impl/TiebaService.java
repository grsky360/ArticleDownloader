package cn.hssnow.dler.articlecore.service.impl;

import cn.hssnow.dler.articlecore.service.BaseService;
import cn.hssnow.dler.articlecore.service.support.Host;
import cn.hssnow.dler.articlecore.util.CharCodeDecode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@Host(name = "tieba", host = "tieba.baidu.com", page = "pn")
public class TiebaService extends BaseService {
	
	@Override
	protected void handlePageAndTitle(String content) {
		Document html = Jsoup.parse(content);
		
		Element page = html.selectFirst("#thread_theme_5 > div:nth-child(1) > ul:nth-child(1) > li:nth-child(2) > span:nth-child(2)");
		if (page != null) {
			this.page = Integer.parseInt(page.text());
		} else {
			this.page = 1;
		}
		
		Element title = html.selectFirst(".core_title_txt");
		if (title != null) {
			this.title = title.text();
		}
	}

	@Override
	protected String handleContent(String content) {
		Document html = Jsoup.parse(content);
		html.outputSettings(new Document.OutputSettings().prettyPrint(false));
		html.select("br").append("\n\n");
		html.select("p").append("\n\n");
		
		Elements elements = html.getElementsByClass("j_d_post_content");
		
		StringBuilder sb = new StringBuilder();
		for (Element element : elements) {
			for (Element img : element.select("img")) {
				imgs.add(img.attr("src"));
			}
			sb.append(CharCodeDecode.replace(element.html()
					.replaceAll("<img.*?src=[\"'](.*?)[\"'].*?>", "$1")
			)).append("\n\n");
		}
		return sb.toString();
	}
	
}
