package ilio.adler;

import com.alibaba.fastjson.JSON;
import ilio.adler.service.ArticleService;
import ilio.adler.service.data.Task;
import ilio.adler.service.data.TaskConfig;
import ilio.adler.service.enums.ArticleTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@SpringBootApplication
public class ArticleDownloaderApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ArticleDownloaderApplication.class, args);
	}

	@Autowired private List<ArticleService> articleServices;

	@Override
	public void run(String... args) {
		TaskConfig taskConfig = args.length > 0 ? TaskConfig.load(args[0]) : TaskConfig.load();
		if (taskConfig == null || taskConfig.isNullOrEmpty()) {
			log.warn("Task list is empty");
			return;
		}
		List<Task> tasks = taskConfig.getTasks();
		Map<ArticleTypeEnum, ArticleService> articleServiceMap =
			articleServices.stream().collect(Collectors.toMap(ArticleService::getArticleType, Function.identity()));
		List<Task> errors = new ArrayList<>();
		for (Task task : tasks) {
			task.setPath(taskConfig.getPath());
			ArticleTypeEnum articleType = task.getArticleType();
			if (articleType == null || !articleServiceMap.containsKey(articleType)) {
				errors.add(task);
			}
			ArticleService articleService = articleServiceMap.get(articleType);
			boolean success = articleService.submit(task);
			if (!success) {
				errors.add(task);
			}
		}
		System.out.println(JSON.toJSONString(errors, true));
	}
}
