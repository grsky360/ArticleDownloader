package ilio.adler.util;

import okhttp3.ConnectionPool;
import okhttp3.Cookie;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class HttpClient {

	private static final Map<String, List<Cookie>> COOKIE_STORE = new HashMap<>();

	public static List<Cookie> cookies(HttpUrl httpUrl) {
		if (httpUrl == null) return Collections.emptyList();
		return COOKIE_STORE.computeIfAbsent(httpUrl.host(), k -> new ArrayList<>());
	}

	public enum HttpMethod {
		GET,
		POST
	}

	private static class Singleton {
		private static final OkHttpClient OK_HTTP_CLIENT = getUnsafeHttpClient();

		private static OkHttpClient.Builder builder() {
			return new OkHttpClient.Builder()
				.retryOnConnectionFailure(true)
				.connectionPool(new ConnectionPool(200, 5, TimeUnit.MINUTES))
				.connectTimeout(30, TimeUnit.SECONDS)
				.readTimeout(30, TimeUnit.SECONDS)
				.writeTimeout(30, TimeUnit.SECONDS);
		}

		private static OkHttpClient getUnsafeHttpClient() {
			try {
				X509TrustManager trustManager = new X509TrustManager() {
					@Override
					public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
					}
					@Override
					public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
					}
					@Override
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
						return new java.security.cert.X509Certificate[]{};
					}
				};

				final SSLContext sslContext = SSLContext.getInstance("SSL");
				sslContext.init(null, new TrustManager[]{trustManager}, new java.security.SecureRandom());

				return builder().sslSocketFactory(sslContext.getSocketFactory(), trustManager)
					.hostnameVerifier((hostname, session) -> true)
					.build();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static OkHttpClient client() {
		return Singleton.OK_HTTP_CLIENT;
	}

	public static ResponseBody execute(String url, Map<String, String> header, Map<String, String> data, HttpMethod method) throws IOException {
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
				cookies.addAll(Cookie.parseAll(request.url(), response.headers()));
				return response.body();
			}
		}
		return null;
	}

	public static InputStream download(String url) {
		try {
			ResponseBody responseBody = execute(url, Collections.emptyMap(), Collections.emptyMap(), HttpMethod.GET);
			return responseBody != null ? responseBody.byteStream() : null;
		} catch (IOException e) {
			return null;
		}
	}

	public static String get(String url) throws IOException {
		return get(url, Collections.emptyMap());
	}

	public static String get(String url, Map<String, String> header) throws IOException {
		ResponseBody responseBody = execute(url, header, Collections.emptyMap(), HttpMethod.GET);
		return responseBody != null ? responseBody.string() : null;
	}

	public static String post(String url, Map<String, String> data) throws IOException {
		return post(url, Collections.emptyMap(), data);
	}

	public static String post(String url, Map<String, String> header, Map<String, String> data) throws IOException {
		ResponseBody responseBody = execute(url, header, data, HttpMethod.POST);
		return responseBody != null ? responseBody.string() : null;
	}

}
