package hsnss.article.config;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public abstract class Config {

	final String configFileName;
	protected Document doc;
	protected Element root;

	/**
	 * 检查配置文件
	 */
	protected abstract void check();

	public Config(String configFileName) {
		this.configFileName = configFileName;
		check();
	}

	public void save() {
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("utf8");

		try {
			XMLWriter writer = new XMLWriter(new OutputStreamWriter(new FileOutputStream(configFileName), "utf8"), format);
			writer.write(doc);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
