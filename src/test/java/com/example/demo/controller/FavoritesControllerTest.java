package com.example.demo.controller;

import com.example.demo.model.Priority;
import com.example.demo.model.Task;
import com.example.demo.repository.TaskRepository;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class FavoritesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
    }

    @Test
    void shouldAddAndReturnFavoriteTasks() throws Exception {
        Task task = taskRepository.save(task("Favorite task"));
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/api/favorites/{taskId}", task.getId()).session(session))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/favorites").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(task.getId()));
    }

    @Test
    void shouldReturnNotFoundWhenAddingMissingTaskToFavorites() throws Exception {
        mockMvc.perform(post("/api/favorites/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRemoveTaskFromFavorites() throws Exception {
        Task task = taskRepository.save(task("Favorite task"));
        MockHttpSession session = new MockHttpSession();
        mockMvc.perform(post("/api/favorites/{taskId}", task.getId()).session(session))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/favorites/{taskId}", task.getId()).session(session))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/favorites").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
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
