package cn.hssnow.dler.articlecore.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author hyia
 */
public class CharCodeDecode {

	private static final Map<String, String> CHAR_CODES= new HashMap<String, String>() {
        private static final long serialVersionUID = 5476400268703720423L;

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

	public static String replace(String source) {
	    for (Map.Entry<String, String> entry : CHAR_CODES.entrySet()) {
	        source = source.replaceAll(entry.getKey(), entry.getValue());
        }
        return source;
	}
}
