package ilio.adler.service.data;

import com.alibaba.fastjson.JSONReader;
import com.alibaba.fastjson.TypeReference;
import lombok.Data;

import java.io.FileReader;
import java.util.Collections;
import java.util.List;

/**
 * @author zqy
 * @version 1.0
 * @date 2020/1/19 17:50
 */
@Data
public class TaskConfig {
    private String path;
    private List<Task> tasks;

    public boolean isNullOrEmpty() {
        return path == null || tasks == null || tasks.isEmpty();
    }

    public static final String DEFAULT_JSON_FILE = "task.json";

    public static TaskConfig load() {
        return load(DEFAULT_JSON_FILE);
    }

    public static TaskConfig load(String file) {
        try (JSONReader reader = new JSONReader(new FileReader(file))) {
            return reader.readObject(TaskConfig.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
