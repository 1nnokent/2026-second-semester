package com.example.demo.service;

import com.example.demo.exception.TaskNotFoundException;
import com.example.demo.model.Task;
import com.example.demo.repository.TaskRepository;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final AtomicInteger idSequence = new AtomicInteger(1);

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @PostConstruct
    public void postConstruct() {
        int nextId = taskRepository.getAll().stream()
                .mapToInt(Task::getId)
                .max()
                .orElse(0) + 1;
        idSequence.set(nextId);
    }

    public Task addTask(Task task) {
        task.setId(idSequence.getAndIncrement());
        task.setCreatedAt(LocalDateTime.now());
        taskRepository.add(task);
        return task;
    }

    public void deleteTask(int taskId) {
        getTask(taskId);
        taskRepository.delete(taskId);
    }

    public Task getTask(int taskId) {
        Task task = taskRepository.get(taskId);
        if (task == null) {
            throw new TaskNotFoundException(taskId);
        }
        return task;
    }

    public Task update(int taskId, Task task) {
        getTask(taskId);
        taskRepository.update(taskId, task);
        return task;
    }

    public List<Task> getAllTasks() {
        return taskRepository.getAll();
    }

    public int getTaskCount() {
        return taskRepository.getAll().size();
    }

    public List<Task> getTasksByIds(Collection<Integer> taskIds) {
        return taskIds.stream()
                .map(taskRepository::get)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }
}
