package cn.hssnow.dler.articlecore.service.impl;

import cn.hssnow.dler.articlecore.service.BaseService;
import cn.hssnow.dler.articlecore.service.support.Host;
import cn.hssnow.dler.articlecore.util.CharCodeDecode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Pattern;

@Host(name = "tieba", host = "tieba.baidu.com", page = "pn")
public class TiebaService extends BaseService {
	private static final Pattern PAGE_PATTERN = Pattern.compile("l_reply_num[\\s\\S]*?<span[\\s]*?class=\"red\"[\\s]*?>([0-9]+)");
	
	private static final Pattern TITLE_PATTERN = Pattern.compile("core_title_txt.*?>(.*?)<");
	
	@Override
	protected void handlePageAndTitle(String content) {
		Document html = Jsoup.parse(content);
		
		Element page = html.selectFirst("#thread_theme_5 > div:nth-child(1) > ul:nth-child(1) > li:nth-child(2) > span:nth-child(2)");
		if (page != null) {
			setPage(Integer.parseInt(page.text()));
		} else {
			setPage(1);
		}
		
		Element title = html.selectFirst(".core_title_txt");
		if (title != null) {
			setTitle(title.text());
		}
		System.out.println(getTitle());
	}

	@Override
	protected String handleContent(String content) {
		Document html = Jsoup.parse(content);
		html.outputSettings(new Document.OutputSettings().prettyPrint(false));
		html.select("br").append("\\n");
		html.select("p").append("\\n\\n");
		
		Elements elements = html.getElementsByClass("j_d_post_content");
		
		StringBuilder sb = new StringBuilder();
		for (Element element : elements) {
			for (Element img : element.select("img")) {
				getImgs().add(img.attr("src"));
			}
			sb.append(CharCodeDecode
					.replace(element.html()
							.replaceAll("\\\\n", "\n")
							.replaceAll("<img.*?src=[\"'](.*?)[\"'].*?>", "$1")
					)
			).append("\n\n");
		}
		return sb.toString();
	}
	
}
