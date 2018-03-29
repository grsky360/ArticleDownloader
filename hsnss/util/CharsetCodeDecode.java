package hsnss.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CharsetCodeDecode {

	private static Map<String, String> CHARSET_CODES = new HashMap<String, String>();

	static {
		CHARSET_CODES.put("&#x273F", "✿");
		CHARSET_CODES.put("&#x203F", "‿");
		CHARSET_CODES.put("&#x25E1", "◡");

		CHARSET_CODES.put("&nbsp;", " ");
		CHARSET_CODES.put("&quot;", "\"");
		CHARSET_CODES.put("&amp;", "&");
		CHARSET_CODES.put("&lt;", "<");
		CHARSET_CODES.put("&gt;", ">");

		CHARSET_CODES.put("<(br|BR).*?>", "\r\n");
		CHARSET_CODES.put("<[^>]+>", "");
		CHARSET_CODES.put("[ ]{4}", "");
		CHARSET_CODES.put("[ ]{2}", " ");
	}

	public static void put(String k, String v) {
		CHARSET_CODES.put(k, v);
	}

	public static Set<String> getEncode() {
		return CHARSET_CODES.keySet();
	}

	public static String replace(String source, String rep) {
		return source.replaceAll(rep, CHARSET_CODES.get(rep));
	}
}
