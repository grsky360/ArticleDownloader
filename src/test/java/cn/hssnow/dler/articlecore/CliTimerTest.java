package cn.hssnow.dler.articlecore;

import org.junit.Test;

public class CliTimerTest {

	public static String escapeHTML(String s) {
		StringBuilder out = new StringBuilder(Math.max(16, s.length()));
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c > 127 || c == '"' || c == '<' || c == '>' || c == '&') {
				out.append("&#");
				out.append((int) c);
				out.append(';');
			} else {
				out.append(c);
			}
		}
		return out.toString();
	}

	public static String unescapeHTML(String s) {
		StringBuilder out = new StringBuilder();


		return out.toString();
	}

	@Test
	public void test() {
		System.out.println(escapeHTML("◡"));
	}

}