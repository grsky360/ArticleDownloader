package cn.hssnow.dler.articlecore.host.impl;

import cn.hssnow.dler.articlecore.host.BaseService;
import cn.hssnow.dler.articlecore.host.support.Host;
import cn.hssnow.dler.articlecore.util.HttpRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Host(name = "yamibo", host = "bbs.yamibo.com", page = "page")
public class YamiboService extends BaseService {

	private static final String USERNAME = "parse";
	private static final String PASSWORD = "parse1231";
	private static final String HOST = "http://bbs.yamibo.com";
	private static final String LOGIN_URI = "/member.php?mod=logging&action=login&loginsubmit=yes&handlekey=login&loginhash=LhmL6&inajax=1";
	private static final String REFERER = "/forum.php";
	
	public YamiboService() {
		System.out.println(login());
	}
	
	public boolean login() {
		Map<String, String> data = new HashMap<>();
		data.put("loginfield", "username");
		data.put("username", USERNAME);
		data.put("password", PASSWORD);
		data.put("referer", HOST + REFERER);
		data.put("questionid", "0");
		data.put("answer", "");
		try {
			HttpRequest.post(HOST + LOGIN_URI, data);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	@Override
	protected void handlePageAndTitle(String firstPageContent) {
		
	}

	@Override
	protected String handleContent(String content) {
		return null;
	}

}
