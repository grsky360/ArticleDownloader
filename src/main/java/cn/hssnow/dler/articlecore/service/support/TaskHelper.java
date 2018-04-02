package cn.hssnow.dler.articlecore.service.support;

import com.alibaba.fastjson.JSONReader;
import com.alibaba.fastjson.JSONWriter;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class TaskHelper {

	public static final String DEFAULT_JSON_FILE = "task.json";
	
	public static Tasks load() {
		return load(DEFAULT_JSON_FILE);
	}
	
	public static Tasks load(String file) {
		try (JSONReader reader = new JSONReader(new FileReader(file))) {
			return reader.readObject(Tasks.class);
		} catch (FileNotFoundException ignored) {
			Tasks emptyTasks = Tasks.emptyTasks();
			try (JSONWriter writer = new JSONWriter(new FileWriter(file))) {
				writer.writeObject(emptyTasks);
			} catch (IOException ignored2) {
			}
			return emptyTasks;
		}
	}
	
}
