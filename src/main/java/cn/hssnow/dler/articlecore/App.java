package cn.hssnow.dler.articlecore;

import cn.hssnow.dler.articlecore.factory.impl.ServiceFactoryImpl;
import cn.hssnow.dler.articlecore.service.BaseService;
import cn.hssnow.dler.articlecore.service.support.Task;
import cn.hssnow.dler.articlecore.service.support.TaskHelper;
import cn.hssnow.dler.articlecore.service.support.Tasks;
import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

public class App {
	
	public static void main(String[] args) {
		Tasks tasks;
		if (args.length > 0) {
			tasks = TaskHelper.load(args[0]);
		} else {
			tasks = TaskHelper.load();
		}

		BaseService service;
		
		List<Task> errors = new ArrayList<>();
		for (Task task : tasks) {
			service = new ServiceFactoryImpl().judge(tasks.getPath(), task.getName(), task.getUrl());
			if (!service.run()) {
				errors.add(task);
			}
		}
		System.out.println(JSON.toJSONString(errors));
	}
    
}
