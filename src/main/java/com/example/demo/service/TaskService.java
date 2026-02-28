package com.example.demo.service;

import com.example.demo.model.Task;
import com.example.demo.repository.TaskRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Сервис для управления задачами. Обеспечивает бизнес-логику работы с задачами, делегируя операции
 * хранения данных в TaskRepository. Поддерживает кэширование задач в памяти для оптимизации
 * доступа.
 */
@Service
public class TaskService {

    /**
     * Репозиторий для хранения и получения задач
     */
    private final TaskRepository taskRepository;
    /**
     * Кэш задач в памяти для быстрого доступа
     */
    private Map<String, Task> taskCache;

    /**
     * Название приложения, внедряемое из конфигурации
     */
    @Value("${app.name}")
    private String appName;

    /**
     * Конструктор сервиса с внедрением зависимости репозитория.
     *
     * @param taskRepository репозиторий для работы с задачами
     */
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * Метод инициализации, вызываемый после внедрения всех зависимостей. Инициализирует кэш задач и
     * загружает в него все задачи из репозитория. Выполняется автоматически при создании bean'а
     * Spring.
     */
    @PostConstruct
    public void postConstruct() {
        taskCache = new HashMap<>();

        List<Task> tasks = taskRepository.getAll();
        for (Task task : tasks) {
            taskCache.put(Integer.toString(task.getId()), task);
        }
    }

    /**
     * Метод очистки, вызываемый перед уничтожением bean'а. Логирует количество задач в кэше и
     * очищает кэш. Выполняется автоматически при завершении работы приложения.
     */
    @PreDestroy
    public void preDestroy() {
        System.out.println(
                "TaskService PreDestroy: в taskCache находилось " + taskCache.size() + " задач. "
                        + appName + ". Подписаться.");
        taskCache.clear();
    }

    /**
     * Добавляет новую задачу.
     *
     * @param task задача для добавления
     */
    public void addTask(Task task) {
        taskRepository.add(task);
    }

    /**
     * Удаляет задачу по идентификатору.
     *
     * @param taskId идентификатор задачи для удаления
     */
    public void deleteTask(int taskId) {
        taskRepository.delete(taskId);
    }

    /**
     * Получает задачу по идентификатору.
     *
     * @param taskId идентификатор задачи
     * @return задача с указанным идентификатором
     */
    public Task getTask(int taskId) {
        return taskRepository.get(taskId);
    }

    /**
     * Обновляет существующую задачу.
     *
     * @param taskId идентификатор задачи для обновления
     * @param task   новый объект задачи с обновленными данными
     */
    public void update(int taskId, Task task) {
        taskRepository.update(taskId, task);
    }

    /**
     * Получает список всех задач.
     *
     * @return список всех задач
     */
    public List<Task> getAllTasks() {
        return taskRepository.getAll();
    }
}
