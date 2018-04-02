package cn.hssnow.dler.articlecore.service;

import cn.hssnow.dler.articlecore.service.support.Host;
import cn.hssnow.dler.articlecore.util.CliTimer;
import cn.hssnow.dler.articlecore.util.EpubPacker;
import cn.hssnow.dler.articlecore.util.HttpClient;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class BaseService {
	private static final List<String> SKIP_IMGS = Arrays.asList("static/image/", "bababian.com", "/js/", ".gif", "editor/images/face");
	
    private String path;
    private String filename;
    
    private String url;
    protected String title;
    protected int page = 0;
    
    protected List<String> imgs = new ArrayList<>();

    private boolean init = false;
	
    private String getSavePath(String ext) {
    	if (ext.charAt(0) != '.') {
    		ext = "." + ext;
		}
    	if (path.charAt(path.length() - 1) == File.separatorChar) {
    		return path + filename + ext;
		}
		return path + File.separator + filename + ext;
	}
    
    private String getPageSeparate() {
    	return this.getClass().getAnnotation(Host.class).page();
	}
    
	private String getPageUrl(int page) {
    	int index = url.indexOf('?');
    	if (index == -1) {
    		return url + "?" + getPageSeparate() + "=" + page;
		}
		if (index == url.length() - 1) {
    		return url + getPageSeparate() + "=" + page;
		}
		return url + "&" + getPageSeparate() + "=" + page;
	}
	
    public void build(String path, String filename, String url) {
    	this.path = path;
    	this.filename = filename;
    	this.url = url;
    	
    	handleUrl();
    	
    	init = true;
	}
	
    private void handleUrl() {
    	if (url.indexOf('?') != -1) {
    		StringBuilder sb = new StringBuilder(url.substring(0, url.indexOf('?') + 1));
			String[] params = url.substring(url.indexOf('?') + 1).split("&");
			for (String param : params) {
				if (!param.split("=")[0].equals(getPageSeparate())) {
					sb.append(param).append('&');
				}
			}
			if (sb.charAt(sb.length() - 1) == '&') {
				sb.deleteCharAt(sb.length() - 1);
			}
			url = sb.toString();
		}
	}

	protected abstract void handlePageAndTitle(String content);
	protected abstract String handleContent(String content);
    
    private boolean open() {
		String content = getContent(1);
		handlePageAndTitle(content);

		content = title + "\n" + getPageUrl(1) + "\n" + "Page " + page + " / " + this.page + handleContent(content);
		
		File file = new File(getSavePath("tmp"));
		if (file.exists() && !file.delete()) return false;
		
		if (save(file, content)) {
			for (int page = 2; page <= this.page; page++) {
				content = getPageUrl(page) + "\n" + "Page " + page + " / " + this.page + handleContent(getContent(page));
				if (!save(file, content)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
    
	private String getContent(int page) {
    	String url = getPageUrl(page);
    	String info = url + "\tPage: " + page + "/" + (this.page == 0 ? "?" : this.page);
		CliTimer timer = new CliTimer(info, "OK", 10, 2);

		Map<String, String> header = new HashMap<>();
		header.put("User-Agent", "Mozilla/5.0 (Windows NT 6.3; rv:36.0) Gecko/20100101 Firefox/36.04");
		header.put("Content-Type", "text/html;charset=UTF-8");
		
		timer.start();
		
		String content = null;
		
		try {
			content = HttpClient.get(url, header);
			timer.stop();
		} catch (IOException e) {
			timer.error();
		}
		
		return content == null ? "" : content;
	}
	
	private boolean save(File file, String content) {
		try {
			RandomAccessFile random = new RandomAccessFile(file, "rw");
			random.seek(random.length());
			random.write(content.getBytes());
			random.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public boolean start() {
    	if (!init) return false;
    	
    	if (!open()) return false;
    	
    	File tmp = new File(getSavePath("tmp"));
    	if (!tmp.exists()) return false;
    	
    	List<String> imgs = this.imgs.stream().filter(img -> !SKIP_IMGS.contains(img)).collect(Collectors.toList());
    	
    	if (imgs.isEmpty()) {
			try {
				List<String> lines = Files.readAllLines(tmp.toPath(), StandardCharsets.UTF_8);
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmp), "UTF-8"));
				for (String line : lines) {
					writer.write(line.replaceAll("&lt;", "<").replaceAll("%gt;", ">"));
				}
				writer.flush();
				writer.close();
			} catch (IOException e) {
				return false;
			}
			return tmp.renameTo(new File(getSavePath("txt")));
		}
		
		EpubPacker epubPacker = new EpubPacker(path, filename, title, tmp, imgs);
    	
    	return epubPacker.pack();
	}
	
}
