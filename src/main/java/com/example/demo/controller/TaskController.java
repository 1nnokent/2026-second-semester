package com.example.demo.controller;

import com.example.demo.model.Task;
import com.example.demo.service.TaskService;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * REST контроллер для управления задачами. Предоставляет HTTP endpoints для выполнения CRUD
 * операций над задачами. Все endpoint'ы доступны по базовому пути /api/tasks.
 */
@RestController
@RequestMapping("api/tasks")
public class TaskController {

    private final TaskService taskService;

    /**
     * Конструктор контроллера с внедрением зависимости TaskService.
     *
     * @param taskService сервис для работы с задачами
     */
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Получает список всех задач.
     *
     * @return список всех задач
     */
    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    /**
     * Получает задачу по идентификатору.
     *
     * @param id идентификатор задачи
     * @return задача с указанным идентификатором
     */
    @GetMapping("/{id}")
    public Task getTask(@PathVariable int id) {
        return taskService.getTask(id);
    }

    /**
     * Создает новую задачу.
     *
     * @param task объект задачи для создания
     */
    @PostMapping
    public void createTask(@RequestBody Task task) {
        taskService.addTask(task);
    }

    /**
     * Обновляет существующую задачу.
     *
     * @param id   идентификатор задачи для обновления
     * @param task объект задачи с новыми данными
     */
    @PutMapping("/{id}")
    public void updateTask(@PathVariable int id, @RequestBody Task task) {
        taskService.update(id, task);
    }

    /**
     * Удаляет задачу по идентификатору.
     *
     * @param id идентификатор задачи для удаления
     */
    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable int id) {
        taskService.deleteTask(id);
    }
}
