package hsnss.article.down;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Tieba extends Site {

	public Tieba(String path, String filename, String url, String pn) {
		super(path, filename, url, pn);
	}

	@Override
	protected void parseUrl() {
		String url = this.url;
		Matcher m = Pattern.compile("http.*?[0-9]+").matcher(url);
		if (m.find())
			this.url = m.group() + "?";
		if (url.contains("see_lz=1"))
			this.url += "see_lz=1";
	}

	@Override
	protected void getPageAndTitle() {
		Matcher matcher = Pattern.compile("l_reply_num[\\s\\S]*?<span[\\s]*?class=\"red\"[\\s]*?>([0-9]+)").matcher(fcontent);
		if (matcher.find())
			page = Integer.valueOf(matcher.group(1));
		else
			page = 1;
		matcher = Pattern.compile("core_title_txt.*?>(.*?)<").matcher(fcontent);
		if (matcher.find())
			title = matcher.group(1);
	}

	@Override
	protected String getContentAndImgs(String uc, boolean isUrl, int page) {
		String content = isUrl ? getContent(uc, page) : uc;
		StringBuilder post = new StringBuilder();
		Matcher findContent = Pattern.compile("j_d_post_content.*?>([\\s\\S]*?)</div>[\\s\\S]*?(div.*?post-tail-wrap[\\s\\S]*?div)").matcher(content);
		Matcher findImg;
		Matcher findFloor;
		Pattern findImg_p = Pattern.compile("<img.*?src=\"(.*?)\".*?>");
		Pattern findFloor_p = Pattern.compile("<span.*?tail-info.*?>([0-9].*?)</span>");
		while (findContent.find()) {
			findImg = findImg_p.matcher(findContent.group(1));
			findFloor = findFloor_p.matcher(findContent.group(2));
			String[] floor = new String[2];

			for (int j = 0; findFloor.find(); j++)
				floor[j] = findFloor.group(1);

			post.append(floor[0]);
			post.append("\t");
			post.append(floor[1]);
			post.append("\r\n");
			post.append(findContent.group(1));
			while (findImg.find()) {
				String imgHtml = findImg.group(0);
				String imgUrl = findImg.group(1);
				if (imgUrl.substring(imgUrl.lastIndexOf("/")).contains(".gif")) {
					continue;
				}
				if (imgUrl.contains("editor/images/face")) {
					continue;
				}
				int start = post.indexOf(imgHtml);
				post = post.replace(start, start + imgHtml.length(), imgUrl);
				imgs.add(imgUrl);
			}
			post.append("\r\n");
			post.append("\r\n");
		}
		content = replaceChar(post.toString());

		return content;
	}

}