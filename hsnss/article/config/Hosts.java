package hsnss.article.config;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Hosts extends Config {

	public static class Host {
		public String getId() {
			return id;
		}

		public String getClassName() {
			return className;
		}

		public String getHost() {
			return host;
		}

		public String getPn() {
			return pn;
		}

		private String id;
		private String className;
		private String host;
		private String pn;

		Host(String id, String className, String host, String pn) {
			this.id = id;
			this.className = className;
			this.host = host;
			this.pn = pn;
		}
	}

	private ArrayList<Host> hosts;

	public Hosts() {
		super("config.xml");
		hosts = new ArrayList<Host>();

		for (Object o : root.element("Hosts").elements("Host")) {
			Element e = (Element) o;
			hosts.add(new Host(e.attributeValue("id"), e.attributeValue("class"), e.attributeValue("host"), e.attributeValue("pn")));
		}
	}

	public Host[] getHosts() { return hosts.toArray(new Host[hosts.size()]); }

	public void check() {
		File f = new File(configFileName);
		try {
			if ((f.exists() && f.length() == 0) || (!f.exists() && f.createNewFile())) {
				doc = DocumentHelper.createDocument();
				Element root = doc.addElement("Config").addElement("Hosts");
				root.addElement("Host").addAttribute("id", "tieba").addAttribute("class", "cn.hsnss.down.Tieba").addAttribute("host", "tieba.baidu.com").addAttribute("pn", "pn");
				root.addElement("Host").addAttribute("id", "yamibo").addAttribute("class", "cn.hsnss,down.Yamibo").addAttribute("host", "www.yamibo.com").addAttribute("pn", "page");
				save();
			} else {
				doc = new SAXReader().read(configFileName);
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			root = doc.getRootElement();
		}
	}
}
