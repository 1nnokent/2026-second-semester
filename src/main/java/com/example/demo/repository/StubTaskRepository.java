package com.example.demo.repository;

import com.example.demo.model.Priority;
import com.example.demo.model.Task;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

public class StubTaskRepository implements TaskRepository {

    private final List<Task> tasks = new ArrayList<>();

    public StubTaskRepository() {
        for (int i = 0; i < 10; i++) {
            tasks.add(new Task(
                    i,
                    "Title" + i,
                    "Description" + i,
                    false,
                    LocalDateTime.now().minusDays(i),
                    LocalDate.now().plusDays(i + 1L),
                    Priority.MEDIUM,
                    new LinkedHashSet<>()
            ));
        }
    }

    @Override
    public void add(Task task) {
    }

    @Override
    public void delete(int taskId) {
        Iterator<Task> iterator = tasks.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getId() == taskId) {
                iterator.remove();
                return;
            }
        }
    }

    @Override
    public Task get(int taskId) {
        for (Task task : tasks) {
            if (task.getId() == taskId) {
                return task;
            }
        }
        return null;
    }

    @Override
    public List<Task> getAll() {
        return tasks;
    }

    @Override
    public void update(int taskId, Task task) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId() == taskId) {
                tasks.set(i, task);
                return;
            }
        }
    }

    @Override
    public int find(Task task) {
        return tasks.indexOf(task);
    }
}
