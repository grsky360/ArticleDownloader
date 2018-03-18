package cn.hssnow.dler.articlecore.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author hyia
 */
public class CharCodeDecode {

	public static final Map<String, String> CHAR_CODES= new HashMap<>() {
		{
			put("&#x273F", "✿");
			put("&#x203F", "‿");
			put("&#x25E1", "◡");

			put("&nbsp;", " ");
			put("&quot;", "\"");
			put("&amp;", "&");
			put("&lt;", "<");
			put("&gt;", ">");

			put("<(br|BR).*?>", "\r\n");
			put("<[^>]+>", "");
			put("[ ]{4}", "");
			put("[ ]{2}", " ");
		}
	};

	public static void put(String k, String v) {
		CHAR_CODES.put(k, v);
	}

	public static Set<String> getEncodeChars() {
		return CHAR_CODES.keySet();
	}

	public static String replace(String source, String rep) {
		return source.replaceAll(rep, CHAR_CODES.get(rep));
	}
}
