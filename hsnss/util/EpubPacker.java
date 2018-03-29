package hsnss.util;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubWriter;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class EpubPacker {

	private String path;
	private String filename;
	private String content;
	private String[] imglist;
	private String title;
	private String html;

	public EpubPacker(String path, String filename, String title, InputStream content, String[] imgUrl) {
		this.path = path;
		this.filename = filename;
		this.title = title;

		StringBuilder sb = new StringBuilder();
		int i;
		try {
			InputStreamReader in = new InputStreamReader(content, "utf8");
			while ((i = in.read()) > 0)
				sb.append((char)i);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.content = sb.toString();
		List<String> temp = new ArrayList<String>();
		for (String s : imgUrl) {
			boolean skip = false;
			for (String ss : temp) {
				if (ss.equals(s)) {
					skip = true;
					break;
				}
			}
			if (!skip) {
				temp.add(s);
			}
		}
		this.imglist = temp.toArray(new String[temp.size()]);
		System.out.println("Image number: " + this.imglist.length);
	}

	private void split() {
		for (int i = 0; i < imglist.length; i++) {
			content = content.replaceAll(imglist[i], "\r\n<div style=\"text-align:center\"><img src=\"" + String.valueOf(i) + ".jpg\" /></div>\r\n");
		}
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(content.getBytes("utf8")), "utf8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String s;
		StringBuilder sb = new StringBuilder();
		try {
			while ((s = in != null ? in.readLine() : null) != null) {
				if (!s.contains("div"))
					sb.append("<p>").append(s).append("</p>\r\n");
				else
					sb.append(s).append("\r\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		String body = "<body><div>\r\n" + sb.toString() + "</div></body>\r\n";
		html = "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"zh-CN\">\n" +
				"<head>\n" +
				"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n" +
				"<meta name=\"provider\" content=\"hsnss.cn\"/>\n" +
				"<meta name=\"builder\" content=\"Hyia\"/>\n" +
				"<meta name=\"right\" content=\"Provided by Hyia\"/>\n" +
				"<link rel=\"stylesheet\" type=\"text/css\" href=\"css/main.css\"/>\n" +
				"<title>" + title + "</title>\n" +
				"</head>\r\n" + body + "</html>";
	}

	public void pack() {
		CLITimer timer = new CLITimer("Epub packaging", "OK", 9, 2);
		timer.start();
		split();
		Book book = new Book();
		book.getMetadata().addTitle(title);
		try {
			for (int i = 1; i < imglist.length; i++) {
				HttpURLConnection con = (HttpURLConnection) new URL(imglist[i]).openConnection();
				//System.out.println(imglist[i]);
				con.setConnectTimeout(3000);
				int code;
				try {
					code = con.getResponseCode();
				} catch (SocketTimeoutException e) {
					continue;
				}
				if (code == HttpURLConnection.HTTP_OK || code == HttpURLConnection.HTTP_ACCEPTED || code == HttpURLConnection.HTTP_CREATED || code == HttpURLConnection.HTTP_NO_CONTENT || code == HttpURLConnection.HTTP_RESET) {
					Resource rec = new Resource(con.getInputStream(), String.valueOf(i) + ".jpg");
					if (i == 0)
						book.setCoverImage(rec);
					book.addResource(rec);
					con.disconnect();
				}
			}
			book.addSection(title, new Resource(new ByteArrayInputStream(html.getBytes("utf8")), "content.html"));
			File f = new File(path);
			if (!f.exists())
				f.mkdir();
			EpubWriter writer =  new EpubWriter();
			writer.write(book, new FileOutputStream(new File(path + File.separator + filename + ".epub")));
			timer.stop();
		} catch (IOException e) {
			timer.error();
			e.printStackTrace();
		}
	}

}
