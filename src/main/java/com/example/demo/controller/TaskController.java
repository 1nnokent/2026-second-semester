package com.example.demo.controller;

import com.example.demo.mapper.TaskMapper;
import com.example.demo.model.Task;
import com.example.demo.service.TaskService;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.dto.*;


/**
 * REST контроллер для управления задачами. Предоставляет HTTP endpoints для выполнения CRUD
 * операций над задачами. Все endpoint'ы доступны по базовому пути /api/tasks.
 */
@RestController
@RequestMapping("api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final TaskMapper taskMapper;

    /**
     * Конструктор контроллера с внедрением зависимости TaskService.
     *
     * @param taskService сервис для работы с задачами
     */
    public TaskController(TaskService taskService, TaskMapper taskMapper) {
        this.taskService = taskService;
        this.taskMapper = taskMapper;
    }

    /**
     * Получает список всех задач.
     *
     * @return список всех задач
     */
    @GetMapping
    public List<TaskResponseDto> getAllTasks() {
        List<TaskResponseDto> returner = new ArrayList<>();
        for (Task elem : taskService.getAllTasks()) {
            returner.add(taskMapper.toResponseDto(elem));
        }
        return returner;
    }

    /**
     * Получает задачу по идентификатору.
     *
     * @param id идентификатор задачи
     * @return задача с указанным идентификатором
     */
    @GetMapping("/{id}")
    public TaskResponseDto getTask(@PathVariable int id) {
        return taskMapper.toResponseDto(taskService.getTask(id));
    }

    /**
     * Создает новую задачу.
     *
     * @param task объект задачи для создания
     */
    @PostMapping
    public void createTask(@RequestBody @Validated(OnCreate.class) TaskCreateDto task) {
        taskService.addTask(taskMapper.toEntity(task));
    }

    /**
     * Обновляет существующую задачу.
     *
     * @param id   идентификатор задачи для обновления
     * @param task объект задачи с новыми данными
     */
    @PutMapping("/{id}")
    public void updateTask(@PathVariable int id, @RequestBody @Validated(OnUpdate.class) TaskUpdateDto task) {
        taskMapper.updateEntity(task, taskService.getTask(id));
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
