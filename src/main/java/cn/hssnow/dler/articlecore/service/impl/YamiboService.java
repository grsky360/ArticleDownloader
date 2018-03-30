package cn.hssnow.dler.articlecore.service.impl;

import cn.hssnow.dler.articlecore.service.BaseService;
import cn.hssnow.dler.articlecore.service.support.Host;
import cn.hssnow.dler.articlecore.util.CharCodeDecode;
import cn.hssnow.dler.articlecore.util.HttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Host(name = "yamibo", host = "bbs.yamibo.com", page = "page")
public class YamiboService extends BaseService {

	private static final String USERNAME = "parse";
	private static final String PASSWORD = "parse1231";
	private static final String HOST = "http://bbs.yamibo.com";
	private static final String LOGIN_URI = "/member.php?mod=logging&action=login&loginsubmit=yes&handlekey=login&loginhash=LUy46&inajax=1";
	private static final String REFERER = "/forum.php";
	
	public YamiboService() {
		login();
	}
	
	private boolean login() {
		Map<String, String> data = new HashMap<>();
		data.put("loginfield", "username");
		data.put("username", USERNAME);
		data.put("password", PASSWORD);
		data.put("referer", HOST + REFERER);
		data.put("questionid", "0");
		data.put("answer", "");
		try {
			HttpClient.post(HOST + LOGIN_URI, data);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	@Override
	protected void handlePageAndTitle(String content) {
		Document html = Jsoup.parse(content);
		Element page = html.selectFirst(".pg");
		if (page == null) {
			setPage(1);
		} else {
			String pageTitle = page.select("label span").attr("title");
			setPage(Integer.parseInt(pageTitle.split(" ")[1]));
		}
		Element title = html.selectFirst("#thread_subject");
		if (title != null) {
			setTitle(title.text());
		}
	}

	@Override
	protected String handleContent(String content) {
		Document html = Jsoup.parse(content);
		html.outputSettings(new Document.OutputSettings().prettyPrint(false));

		Elements elements = html.getElementsByClass("t_f");
		StringBuilder sb = new StringBuilder();
		for (Element element : elements) {
			for (Element img : element.select("img")) {
				String src = img.attr("src");
				if (src == null || "".equals(src)) {
					src = img.attr("file");
					if (src == null || "".equals(src)) {
						continue;
					}
				}
				getImgs().add(src);
			}
			sb.append(CharCodeDecode.replace(element.html()
					.replaceAll("<img.*?(src|file)=[\"'](.*?)[\"'].*?>", "$2")
			)).append("\n");
		}
		return sb.toString();
	}

}
