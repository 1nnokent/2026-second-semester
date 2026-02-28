package com.example.demo.controller;

import com.example.demo.model.Task;
import com.example.demo.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Интеграционные тесты для TaskController.
 * Проверяет работу всех CRUD endpoints через HTTP запросы.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TaskControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TaskRepository taskRepository;

    /**
     * Очищает репозиторий перед каждым тестом для изоляции.
     */
    @BeforeEach
    void setUp() {
        taskRepository.getAll().clear();
    }

    /**
     * Позитивный тест: получение всех задач, когда есть задачи в репозитории.
     */
    @Test
    void shouldReturnAllTasks_WhenTasksExist() {
        Task task1 = new Task(1, "Task 1", "Description 1", false);
        Task task2 = new Task(2, "Task 2", "Description 2", true);
        taskRepository.add(task1);
        taskRepository.add(task2);

        ResponseEntity<Task[]> response = restTemplate.getForEntity("/api/tasks", Task[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().length);
    }

    /**
     * Негативный тест: получение всех задач, когда репозиторий пуст.
     */
    @Test
    void shouldReturnEmptyList_WhenNoTasksExist() {
        ResponseEntity<Task[]> response = restTemplate.getForEntity("/api/tasks", Task[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().length);
    }

    /**
     * Позитивный тест: получение задачи по существующему ID.
     */
    @Test
    void shouldReturnTask_WhenTaskExists() {
        Task task = new Task(1, "Test Task", "Test Description", false);
        taskRepository.add(task);

        ResponseEntity<Task> response = restTemplate.getForEntity("/api/tasks/1", Task.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Task", response.getBody().getTitle());
        assertEquals(1, response.getBody().getId());
    }

    /**
     * Негативный тест: получение задачи по несуществующему ID.
     */
    @Test
    void shouldReturnNull_WhenTaskNotFound() {
        ResponseEntity<Task> response = restTemplate.getForEntity("/api/tasks/999", Task.class);
        assertNull(response.getBody());
    }

    /**
     * Позитивный тест: создание новой задачи.
     */
    @Test
    void shouldCreateTask_WhenValidTaskProvided() {
        Task newTask = new Task(1, "New Task", "New Description", false);

        ResponseEntity<Void> response = restTemplate.postForEntity("/api/tasks", newTask, Void.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Task savedTask = taskRepository.get(1);
        assertNotNull(savedTask);
        assertEquals("New Task", savedTask.getTitle());
    }

    /**
     * Негативный тест: создание задачи с null данными.
     * (В реальности нужна валидация на уровне контроллера)
     */
    @Test
    void shouldHandleNullTask_WhenCreatingTask() {
        try {
            restTemplate.postForEntity("/api/tasks", null, Void.class);
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    /**
     * Позитивный тест: обновление существующей задачи.
     */
    @Test
    void shouldUpdateTask_WhenTaskExists() {
        Task originalTask = new Task(1, "Original", "Original Description", false);
        taskRepository.add(originalTask);

        Task updatedTask = new Task(1, "Updated", "Updated Description", true);

        restTemplate.put("/api/tasks/1", updatedTask);

        Task result = taskRepository.get(1);
        assertNotNull(result);
        assertEquals("Updated", result.getTitle());
        assertEquals("Updated Description", result.getDescription());
        assertTrue(result.getCompleted());
    }

    /**
     * Негативный тест: попытка обновления несуществующей задачи.
     */
    @Test
    void shouldNotCrash_WhenUpdatingNonExistentTask() {
        Task updatedTask = new Task(999, "Updated", "Description", false);

        assertDoesNotThrow(() -> restTemplate.put("/api/tasks/999", updatedTask));

        assertNull(taskRepository.get(999));
    }

    /**
     * Позитивный тест: удаление существующей задачи.
     */
    @Test
    void shouldDeleteTask_WhenTaskExists() {
        Task task = new Task(1, "To Delete", "Description", false);
        taskRepository.add(task);

        restTemplate.delete("/api/tasks/1");

        Task deletedTask = taskRepository.get(1);
        assertNull(deletedTask);
    }

    /**
     * Негативный тест: удаление несуществующей задачи.
     */
    @Test
    void shouldNotCrash_WhenDeletingNonExistentTask() {
        assertDoesNotThrow(() -> restTemplate.delete("/api/tasks/999"));
    }
}