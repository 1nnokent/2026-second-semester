package com.example.demo.controller;

import com.example.demo.dto.TaskCreateDto;
import com.example.demo.dto.TaskUpdateDto;
import com.example.demo.model.Priority;
import com.example.demo.model.Task;
import com.example.demo.repository.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
    }

    @Test
    void shouldReturnAllTasksWithTotalCountHeader() throws Exception {
        taskRepository.saveAll(java.util.List.of(task("Task one"), task("Task two")));

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "2"))
                .andExpect(header().string("X-API-Version", "2.0.0"))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void shouldCreateTaskWhenPayloadIsValid() throws Exception {
        TaskCreateDto request = new TaskCreateDto(
                "Prepare report",
                "Collect data for the weekly report",
                LocalDate.now().plusDays(2),
                Priority.HIGH,
                Set.of("work", "report")
        );

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.title").value("Prepare report"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void shouldReturnBadRequestWhenCreatePayloadIsInvalid() throws Exception {
        TaskCreateDto request = new TaskCreateDto(
                "  ",
                "x".repeat(20),
                LocalDate.now().minusDays(1),
                null,
                Set.of("one", "two", "three", "four", "five", "six")
        );

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.details.title").exists());
    }

    @Test
    void shouldReturnTaskById() throws Exception {
        Task task = taskRepository.save(task("Existing task"));

        mockMvc.perform(get("/api/tasks/{id}", task.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(task.getId()))
                .andExpect(jsonPath("$.title").value("Existing task"));
    }

    @Test
    void shouldReturnNotFoundForMissingTask() throws Exception {
        mockMvc.perform(get("/api/tasks/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void shouldUpdateTaskWhenPayloadIsValid() throws Exception {
        Task existingTask = taskRepository.save(task("Old title"));

        TaskUpdateDto request = new TaskUpdateDto(
                "New title",
                "Updated description",
                true,
                LocalDate.now().plusDays(5),
                Priority.LOW,
                Set.of("updated")
        );

        mockMvc.perform(put("/api/tasks/{id}", existingTask.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New title"))
                .andExpect(jsonPath("$.completed").value(true))
                .andExpect(jsonPath("$.priority").value("LOW"));
    }

    @Test
    void shouldReturnBadRequestWhenDueDateIsBeforeCreationDate() throws Exception {
        Task existingTask = taskRepository.save(task("Deadline task"));

        TaskUpdateDto request = new TaskUpdateDto(
                "Deadline task",
                null,
                null,
                LocalDate.now().minusDays(1),
                null,
                null
        );

        mockMvc.perform(put("/api/tasks/{id}", existingTask.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldDeleteTask() throws Exception {
        Task task = taskRepository.save(task("Delete me"));

        mockMvc.perform(delete("/api/tasks/{id}", task.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/tasks/{id}", task.getId()))
                .andExpect(status().isNotFound());
    }

    private Task task(String title) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription("Description for " + title);
        task.setCompleted(false);
        task.setDueDate(LocalDate.now().plusDays(1));
        task.setPriority(Priority.MEDIUM);
        task.setTags(new LinkedHashSet<>());
        return task;
    }
}
