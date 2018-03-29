package cn.hssnow.dler.articlecore.util;

import cn.hssnow.dler.articlecore.host.impl.YamiboService;
import com.alibaba.fastjson.JSON;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.http2.Header;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {

	public enum HttpMethod {
		GET,
		POST
	}
	
	private static class Client {
		private static final OkHttpClient CLIENT = new OkHttpClient.Builder()
				.cookieJar(new CookieJar() {
					private final Map<String, List<Cookie>> cookieStore = new HashMap<>();
					@Override
					public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
						cookieStore.put(httpUrl.host(), list);
					}

					@Override
					public List<Cookie> loadForRequest(HttpUrl httpUrl) {
						List<Cookie> cookies = cookieStore.get(httpUrl.host());
						return cookies != null ? cookies : new ArrayList<>();
					}
				})
				.build();
	}
	
	private static OkHttpClient client() {
		return Client.CLIENT;
	}
	
	public static String execute(String url, Map<String, String> header, Map<String, String> data, HttpMethod method) throws IOException {
		Request.Builder requestBuilder = new Request.Builder();
		header.forEach(requestBuilder::addHeader);
		switch (method) {
			case GET:
				StringBuilder sb = new StringBuilder();
				data.forEach((k, v) -> sb.append('&').append(k).append('=').append(v));
				// TODO: 2018/3/29 need (automatic removal of duplication)
				requestBuilder.url(url + (url.indexOf('?') == -1 ? "?" : "") + sb.toString());
				requestBuilder.get();
				break;
			case POST:
				requestBuilder.url(url);
				FormBody.Builder formBuilder = new FormBody.Builder();
				data.forEach(formBuilder::add);
				requestBuilder.post(formBuilder.build());
				break;
			default:
				return null;
		}
		
		List<Cookie> cookies = client().cookieJar().loadForRequest(HttpUrl.parse(url));
		StringBuilder sb = new StringBuilder();
		for(Cookie cookie : cookies){
			sb.append(cookie.name()).append("=").append(cookie.value()).append(";");
		}
		requestBuilder.addHeader("Cookie", sb.toString());
		// System.out.println(sb.toString());
		
		Request request = requestBuilder.build();
		System.out.println(request.headers("Cookie"));
		Response response = client().newCall(request).execute();
		
		if (response.isSuccessful()) {
			if (response.body() != null) {
				return response.body().string();
			}
		}
		return null;
	}
	
	public static String get(String url, Map<String, String> header) throws IOException {
		return execute(url, header, Collections.emptyMap(), HttpMethod.GET);
	}
	
	public static String get(String url) throws IOException {
		return get(url, Collections.emptyMap());
	}
	
	public static String post(String url, Map<String, String> header, Map<String, String> data) throws IOException {
		return execute(url, header, data, HttpMethod.POST); 
	}
	
	public static String post(String url, Map<String, String> data) throws IOException {
		return post(url, Collections.emptyMap(), data);
	}

	public static void main(String[] args) throws IOException {
		YamiboService service = new YamiboService();

		//todo: test cookie
		System.out.println(get("https://bbs.yamibo.com/forum.php?mod=viewthread&tid=182132&page=1&authorid=11241"));
		
	}
	
}
