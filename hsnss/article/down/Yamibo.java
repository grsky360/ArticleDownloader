package hsnss.article.down;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Yamibo extends Site {

	private static final String username = "parse";
	private static final String password = "parse1231";
	private static final String host = "http://bbs.yamibo.com";
	private static final String posturl = "/member.php?mod=logging&action=login&loginsubmit=yes&handlekey=login&loginhash=LhmL6&inajax=1";
	private static final String refer = "/forum.php";

	public Yamibo(String path, String filename, String url, String pn) {
		super(path, filename, url, pn);
		try {
			login();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	void parseUrl() {
		if (url.contains("?" + pn) || url.contains("&" + pn)) {
			url = url.replaceAll(pn + "=[0-9]+&", "");
		}
	}

	private void login() throws IOException {
		client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(host + posturl);
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("loginfield", "username"));
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("password", password));
		params.add(new BasicNameValuePair("referer", host + refer));
		params.add(new BasicNameValuePair("questionid", "0"));
		params.add(new BasicNameValuePair("answer", ""));
		post.setEntity(new UrlEncodedFormEntity(params));
		client.execute(post);
		post.releaseConnection();
    }

	@Override
	void getPageAndTitle() {
		Matcher matcher = Pattern.compile("<span title=\".*?\\s([0-9]+).*?\">.*?</span>").matcher(fcontent);
		if (matcher.find())
			page = Integer.valueOf(matcher.group(1));
		else
			page = 1;
		matcher = Pattern.compile("<span.*?thread_subject.*?>(.*?)</span>").matcher(fcontent);
		if (matcher.find())
			title = matcher.group(1);
	}

	@Override
	String getContentAndImgs(String uc, boolean isUrl, int page) {
		String content = isUrl ? getContent(uc, page) : uc;
		Matcher findContent = Pattern.compile("(<div id=\"post_[0-9]+.*>)[\\s\\S]*?(<em>[0-9]+</em><sup>#</sup>[\\s\\S]*?<em.*?authorposton[0-9]+.*?>.*?</em>)[\\s\\S]*?(<div class=\"pcb\".*>)([\\s\\S]*?)(<div id=\"comment)").matcher(content);
		Matcher findImg;
		Matcher findFloor;
		Pattern findImg_p = Pattern.compile("<img.*?(src|file)=\"(.*?)\".*?>");
		Pattern findFloor_p = Pattern.compile("<em>([0-9]+)</em>[\\s\\S]*?authorposton[0-9]+.*?>(.*?)</em>");

		StringBuilder post = new StringBuilder();
		while (findContent.find()) {
			findImg = findImg_p.matcher(findContent.group(4));
			findFloor = findFloor_p.matcher(findContent.group(2));
			findFloor.find();
			post.append(findFloor.group(1)).append("#\t").append(findFloor.group(2)).append("\r\n");
			post.append(findContent.group(4));
			while (findImg.find()) {
				String imgHtml = findImg.group(0);
				String imgUrl = findImg.group(2);
				if (!imgUrl.contains("http")) {
					imgUrl = host + "/" + imgUrl;
				}
				if (imgUrl.contains("static/image/") || imgUrl.contains("bababian.com") || imgUrl.contains("/js/") || imgUrl.contains("back.gif")) {
					continue;
				}
				int start = post.indexOf(imgHtml);
				post = post.replace(start, start + imgHtml.length(), imgUrl);
				imgs.add(imgUrl);
			}
			post.append("\r\n").append("\r\n");
		}
		content = replaceChar(post.toString());

		return content;
	}

}
