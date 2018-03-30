package cn.hssnow.dler.articlecore.service;

import cn.hssnow.dler.articlecore.service.support.Host;
import cn.hssnow.dler.articlecore.util.CliTimer;
import cn.hssnow.dler.articlecore.util.HttpClient;
import lombok.Data;
import okhttp3.OkHttpClient;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public abstract class BaseService {
    
    private String path;
    private String filename;
    
    private String url;
    private String title;
    private int page = 0;
    
    private List<String> imgs = new ArrayList<>();

    OkHttpClient client = new OkHttpClient.Builder().build();
    
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
    
    public boolean open() {
		String content = getContent(1);
		handlePageAndTitle(content);

		content = handleContent(content);
		
		File file = new File(getSavePath("tmp"));
		if (file.exists() && !file.delete()) return false;
		
		if (!save(file, content)) return false;
		
		for (int page = 2; page <= this.page; page++) {
			content = handleContent(getContent(page));
			
			if (!save(file, content)) {
				return false;
			}
		}
		
		return true;
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
	
	private synchronized boolean save(File file, String content) {
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
	
}
