package com.example.demo.service;

import com.example.demo.exception.TaskBulkOperationException;
import com.example.demo.exception.TaskNotFoundException;
import com.example.demo.model.Task;
import com.example.demo.repository.TaskRepository;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional
    public Task addTask(Task task) {
        return taskRepository.save(task);
    }

    @Transactional
    public void deleteTask(Long taskId) {
        Task task = getTask(taskId);
        taskRepository.delete(task);
    }

    public Task getTask(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
    }

    @Transactional
    public Task update(Long taskId, Task task) {
        getTask(taskId);
        return taskRepository.save(task);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public List<Task> getAllTasksWithAttachments() {
        List<Task> tasks = taskRepository.findAllWithAttachments();
        tasks.forEach(task -> task.getAttachments().size());
        return tasks;
    }

    public List<Task> getTasksDueWithinNext7Days() {
        return taskRepository.findTasksDueWithinNext7Days(java.time.LocalDate.now(),
                java.time.LocalDate.now().plusDays(7));
    }

    public long getTaskCount() {
        return taskRepository.count();
    }

    public List<Task> getTasksByIds(Collection<Long> taskIds) {
        if (taskIds == null || taskIds.isEmpty()) {
            return List.of();
        }

        List<Long> orderedIds = taskIds.stream().toList();
        Map<Long, Task> tasksById = new LinkedHashMap<>();
        taskRepository.findAllById(orderedIds).forEach(task -> tasksById.put(task.getId(), task));

        return orderedIds.stream()
                .map(tasksById::get)
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    @Transactional(
            propagation = Propagation.REQUIRED,
            isolation = Isolation.READ_COMMITTED,
            rollbackFor = TaskBulkOperationException.class
    )
    public void bulkCompleteTasks(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        List<Long> uniqueIds = ids.stream().distinct().toList();
        List<Task> tasks = taskRepository.findAllById(uniqueIds);
        if (tasks.size() != uniqueIds.size()) {
            Set<Long> foundIds = tasks.stream()
                    .map(Task::getId)
                    .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));
            List<Long> missingIds = uniqueIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();
            throw new TaskBulkOperationException(missingIds);
        }

        tasks.forEach(task -> task.setCompleted(true));
    }
}
