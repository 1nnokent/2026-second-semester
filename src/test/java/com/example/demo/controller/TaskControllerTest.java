package com.example.demo.controller;

import com.example.demo.dto.TaskCreateDto;
import com.example.demo.dto.TaskUpdateDto;
import com.example.demo.model.Priority;
import com.example.demo.model.Task;
import com.example.demo.repository.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
        taskRepository.getAll().clear();
    }

    @Test
    void shouldReturnAllTasksWithTotalCountHeader() throws Exception {
        taskRepository.add(task(1, "Task one"));
        taskRepository.add(task(2, "Task two"));

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
        taskRepository.add(task(7, "Existing task"));

        mockMvc.perform(get("/api/tasks/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
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
        Task existingTask = task(5, "Old title");
        existingTask.setDueDate(LocalDate.now().plusDays(3));
        taskRepository.add(existingTask);

        TaskUpdateDto request = new TaskUpdateDto(
                "New title",
                "Updated description",
                true,
                LocalDate.now().plusDays(5),
                Priority.LOW,
                Set.of("updated")
        );

        mockMvc.perform(put("/api/tasks/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New title"))
                .andExpect(jsonPath("$.completed").value(true))
                .andExpect(jsonPath("$.priority").value("LOW"));
    }

    @Test
    void shouldReturnBadRequestWhenDueDateIsBeforeCreationDate() throws Exception {
        Task existingTask = task(10, "Deadline task");
        existingTask.setCreatedAt(LocalDateTime.now());
        taskRepository.add(existingTask);

        TaskUpdateDto request = new TaskUpdateDto(
                "Deadline task",
                null,
                null,
                LocalDate.now().minusDays(1),
                null,
                null
        );

        mockMvc.perform(put("/api/tasks/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldDeleteTask() throws Exception {
        taskRepository.add(task(15, "Delete me"));

        mockMvc.perform(delete("/api/tasks/15"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/tasks/15"))
                .andExpect(status().isNotFound());
    }

    private Task task(int id, String title) {
        return new Task(
                id,
                title,
                "Description for " + title,
                false,
                LocalDateTime.now().minusHours(1),
                LocalDate.now().plusDays(1),
                Priority.MEDIUM,
                new LinkedHashSet<>()
        );
    }
}
