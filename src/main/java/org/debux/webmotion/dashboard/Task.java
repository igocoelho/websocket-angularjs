package org.debux.webmotion.dashboard;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Task {

	public static class SortedTasks {
		List<Task> todoTasks = new ArrayList<Task>();
		List<Task> progressTasks = new ArrayList<Task>();
		List<Task> doneTasks = new ArrayList<Task>();
	}

	public static SortedTasks getSortedTasks(List<Task> tasks) {
		SortedTasks sortedTasks = new SortedTasks();

		for (Task task : tasks) {
			String status = task.getStatus();

			if ("todoTasks".equals(status)) {
				sortedTasks.todoTasks.add(task);
			} else if ("progressTasks".equals(status)) {
				sortedTasks.progressTasks.add(task);
			} else if ("doneTasks".equals(status)) {
				sortedTasks.doneTasks.add(task);
			}
		}

		return sortedTasks;
	}

	protected String id;
	protected String name;
	protected String status;

	public Task(String name) {
		this.id = UUID.randomUUID().toString();
		this.name = name;
		this.status = "todoTasks";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
