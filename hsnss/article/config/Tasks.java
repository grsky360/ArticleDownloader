package hsnss.article.config;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Tasks extends Config {

	public static class Task {
		public String getName() {
			return name;
		}

		public String getValue() {
			return value;
		}

		private String name;
		private String value;

		Task(String name, String value) {
			this.name = name;
			this.value = value;
		}
	}

	private ArrayList<Task> tasks;
	private String path;

	public Tasks() {
		super("task.xml");
		tasks = new ArrayList<Task>();

		for (Object o : root.element("Tasks").elements("Task")) {
			Element e = (Element) o;
			tasks.add(new Task(e.element("name").getText(), e.element("url").getText()));
		}
		path = root.element("Path").attributeValue("value");
	}

	public Task[] getTasks() { return tasks.toArray(new Task[tasks.size()]); }
	public String getPath() { return path; }

	public void check() {
		File f = new File(configFileName);
		try {
			if ((f.exists() && f.length() == 0) || (!f.exists() && f.createNewFile())) {
				doc = DocumentHelper.createDocument();
				Element root = doc.addElement("Config").addElement("Tasks");
				root.addComment("\n<Task>\n\t<name></name>\n\t<url><![CDATA[]]></url>\n</Task>\n");
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
