package cn.hssnow.dler.articlecore.host;

import cn.hssnow.dler.articlecore.host.support.Host;
import lombok.Data;
import okhttp3.OkHttpClient;
import java.util.List;

@Data
public abstract class BaseService {
    
    private String path;
    private String filename;
    
    private String url;
    private String title;
    private int page;
    
    private List<String> urls;
    private List<String> imgs;

    OkHttpClient client;
    
    private String getPageSeparate() {
    	return this.getClass().getAnnotation(Host.class).page();
	}
    
    void build(String url) {
    	this.url = url;
    	
    	handleUrl();
	}
	
    private void handleUrl() {
    	if (url.indexOf('?') != -1) {
    		StringBuilder sb = new StringBuilder(url.substring(0, url.indexOf('?') + 1));
			System.out.println(sb.toString());
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
		} else {
    		url += '?';
		}
		
	}

	protected abstract void handlePageAndTitle();
	
    private void open() {
    	// get page 1
		handlePageAndTitle();
		for (int i = 1; i < page; i++) {
			urls.add(url + '&' + getPageSeparate() + i);
		}
	}
    
    
    
	public static void main(String[] args) {
    	final String demo1 = "https://bbs.yamibo.com/forum.php?mod=viewthread&tid=211304&page=2&authorid=224668";
    	final String demo2 = "https://tieba.baidu.com/p/4402297316?see_lz=1";
		
	}
	
}
