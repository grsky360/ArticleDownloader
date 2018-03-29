package hsnss.article.down;

import cn.hsnss.util.CLITimer;
import cn.hsnss.util.CharsetCodeDecode;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.util.ArrayList;

public abstract class Site {

	private String path;
	private String filename;
	private String content;
	String pn;
	String url;
	String title;
	int page;
	String fcontent;
	ArrayList<String> urls;
	ArrayList<String> imgs;
	CloseableHttpClient client;

	Site(String path, String filename, String url, String pn) {
		this.path = path;
		this.filename = filename;
		page = 1;
		fcontent = content = "";
		urls = new ArrayList<String>();
		imgs = new ArrayList<String>();
		this.url = url;
		this.pn = pn;
		parseUrl();
		this.pn = "&" + pn + "=";
		client = HttpClientBuilder.create().build();
	}

	abstract void parseUrl();
	abstract void getPageAndTitle();
	abstract String getContentAndImgs(String uc, boolean isUrl, int page);

	public String[] getImgs() { return imgs.toArray(new String[imgs.size()]); }

	public String getFilename() { return filename; }

	public String getTitle() { return title; }

	public ByteArrayInputStream getContent() throws UnsupportedEncodingException { return new ByteArrayInputStream(content.getBytes("utf8")); }

	private void openUrl() {
		fcontent = getContent(url + pn + "1", 1);
		getPageAndTitle();
		for (int i = 0; i < page; i++) {
			urls.add(url + pn + String.valueOf(i + 1));
		}
		fcontent = getContentAndImgs(fcontent, false, 1);
	}

	private void close() {
		try {
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	String getContent(String url, int page) {
		String info = url + "\tPage: " + page + "/" + this.page;
		CLITimer timer = new CLITimer(info, "OK", 10, 2);
		String content = null;
		timer.start();
		try {
			HttpGet get = new HttpGet(url);
			get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; rv:36.0) Gecko/20100101 Firefox/36.04");
			get.setHeader("Content-Type","text/html;charset=UTF-8");
			CloseableHttpResponse response = client.execute(get);

			content = EntityUtils.toString(response.getEntity(), "utf8");
		/*	OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream("text.txt"), "utf8");
			out.write(content);
			out.close();
			System.exit(0);*/ 
			response.close();
			get.releaseConnection();

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		timer.stop();
		return content;
	}

	public void start() {
		System.out.println(filename);
		long t1 = System.currentTimeMillis();
		openUrl();
		StringBuilder content = new StringBuilder();
		content.append(title).append("\r\n").append(url).append("\r\n").append(fcontent);
		for (int i = 1; i < page; i++) {
			String con = "Page " + String.valueOf(i + 1) + "\r\n" + getContentAndImgs(urls.get(i), true, i + 1);
			content.append(con);
		}
		this.content = content.toString().replaceAll("\n[\\s| ]*\r", "\n\r");
		//this.content = content.toString();
		long t2 = System.currentTimeMillis();
		System.out.println("Download success! Time: " + ((t2 - t1) / 1000.0) + " s");
		close();
	}

	public void save() {
		OutputStreamWriter out;
		try {
			File f = new File(path);
			if (!f.exists())
				f.mkdir();
			out = new OutputStreamWriter(new FileOutputStream(path + File.separator + filename + ".txt"), "utf8");
			out.write(content);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	static String replaceChar(String content) {
		String rep = content;
		for (String s : CharsetCodeDecode.getEncode()) {
			rep = CharsetCodeDecode.replace(rep, s);
		}
		return rep;
	}

}