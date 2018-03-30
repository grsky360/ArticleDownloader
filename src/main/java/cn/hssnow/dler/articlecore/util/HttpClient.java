package cn.hssnow.dler.articlecore.util;

import okhttp3.ConnectionPool;
import okhttp3.Cookie;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HttpClient {

	// TODO: 2018/3/30 I think I need a cookie manager
	private static final Map<String, List<Cookie>> COOKIE_STORE = new HashMap<>();

	public static List<Cookie> cookies(HttpUrl httpUrl) {
		if (httpUrl == null) return Collections.emptyList();
		List<Cookie> cookies = COOKIE_STORE.get(httpUrl.host());
		return cookies != null ? cookies : Collections.emptyList();
	}
	
	public enum HttpMethod {
		GET,
		POST
	}
	
	private static class Singleton {
		private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient.Builder()
				.retryOnConnectionFailure(true)
				.connectionPool(new ConnectionPool(200, 5, TimeUnit.MINUTES))
				.connectTimeout(30, TimeUnit.SECONDS)
				.readTimeout(30, TimeUnit.SECONDS)
				.writeTimeout(30,TimeUnit.SECONDS)
				.build();
	}
	
	private static OkHttpClient client() {
		return Singleton.OK_HTTP_CLIENT;
	}
	
	public static String execute(String url, Map<String, String> header, Map<String, String> data, HttpMethod method) throws IOException {
		Request.Builder requestBuilder = new Request.Builder();
		
		StringBuilder cookieStr = new StringBuilder();
		cookies(HttpUrl.parse(url)).forEach(cookie -> cookieStr.append(cookie.name()).append("=").append(cookie.value()).append(";"));

		header.forEach(requestBuilder::addHeader);
		requestBuilder.addHeader("Cookie", cookieStr.toString());
		
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
		
		Request request = requestBuilder.build();
		Response response = client().newCall(request).execute();
		
		if (response.isSuccessful()) {
			if (response.body() != null) {
				List<Cookie> cookies = cookies(request.url());
				if (cookies == null || cookies.isEmpty()) {
					cookies = Cookie.parseAll(request.url(), response.headers());
					COOKIE_STORE.put(request.url().host(), cookies);
				}
				
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
	
}
