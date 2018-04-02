package cn.hssnow.dler.articlecore.service.support;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

public class Tasks implements Iterable<Task> {
	private static final Tasks EMPTY_TASKS = new Tasks();

	public static Tasks emptyTasks() {
		return EMPTY_TASKS;
	}
	
	private String path;
	private List<Task> tasks = new ArrayList<>();
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public List<Task> getTasks() {
		return tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}

	@Override
	public Iterator<Task> iterator() {
		return tasks.iterator();
	}

	@Override
	public void forEach(Consumer<? super Task> action) {
		tasks.forEach(action);
	}

	@Override
	public Spliterator<Task> spliterator() {
		return tasks.spliterator();
	}

}
