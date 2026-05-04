package com.example.demo.controller;

import com.example.demo.model.Priority;
import com.example.demo.model.Task;
import com.example.demo.repository.TaskAttachmentRepository;
import com.example.demo.repository.TaskRepository;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "app.upload.dir=target/test-uploads")
class AttachmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskAttachmentRepository attachmentRepository;

    @BeforeEach
    void setUp() throws Exception {
        taskRepository.getAll().clear();
        attachmentRepository.getAll().clear();
        Files.createDirectories(Path.of("target/test-uploads"));
        Files.list(Path.of("target/test-uploads")).forEach(path -> {
            try {
                Files.deleteIfExists(path);
            } catch (Exception ignored) {
            }
        });
    }

    @Test
    void shouldUploadAttachmentForExistingTask() throws Exception {
        taskRepository.add(task(1, "Task with file"));
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "notes.txt",
                "text/plain",
                "hello attachment".getBytes()
        );

        mockMvc.perform(multipart("/api/tasks/1/attachments").file(file))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fileName").value("notes.txt"))
                .andExpect(jsonPath("$.size").value(file.getSize()));
    }

    @Test
    void shouldReturnNotFoundWhenUploadingAttachmentForMissingTask() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "notes.txt",
                "text/plain",
                "hello attachment".getBytes()
        );

        mockMvc.perform(multipart("/api/tasks/99/attachments").file(file))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldListAttachmentsForTask() throws Exception {
        taskRepository.add(task(2, "Task list attachments"));
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "list.txt",
                "text/plain",
                "content".getBytes()
        );
        mockMvc.perform(multipart("/api/tasks/2/attachments").file(file))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/tasks/2/attachments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void shouldDownloadAttachment() throws Exception {
        taskRepository.add(task(3, "Task download"));
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "download.txt",
                "text/plain",
                "download me".getBytes()
        );
        mockMvc.perform(multipart("/api/tasks/3/attachments").file(file))
                .andExpect(status().isCreated());

        Long attachmentId = attachmentRepository.getAll().getFirst().id();

        mockMvc.perform(get("/api/attachments/{attachmentId}", attachmentId))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString("download.txt")))
                .andExpect(content().string("download me"));
    }

    @Test
    void shouldReturnNotFoundWhenDownloadingMissingAttachment() throws Exception {
        mockMvc.perform(get("/api/attachments/12345"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteAttachment() throws Exception {
        taskRepository.add(task(4, "Task delete attachment"));
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "delete.txt",
                "text/plain",
                "delete me".getBytes()
        );
        mockMvc.perform(multipart("/api/tasks/4/attachments").file(file))
                .andExpect(status().isCreated());

        Long attachmentId = attachmentRepository.getAll().getFirst().id();

        mockMvc.perform(delete("/api/attachments/{attachmentId}", attachmentId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/attachments/{attachmentId}", attachmentId))
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
                Priority.HIGH,
                new LinkedHashSet<>()
        );
    }
}
