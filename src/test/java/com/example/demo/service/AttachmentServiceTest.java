package com.example.demo.service;

import com.example.demo.exception.AttachmentNotFoundException;
import com.example.demo.model.Priority;
import com.example.demo.model.Task;
import com.example.demo.model.TaskAttachment;
import com.example.demo.repository.TaskAttachmentRepository;
import com.example.demo.repository.TaskRepository;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = "app.upload.dir=target/test-service-uploads")
class AttachmentServiceTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskAttachmentRepository attachmentRepository;

    @Autowired
    private AttachmentService attachmentService;

    @BeforeEach
    void setUp() throws Exception {
        attachmentRepository.deleteAll();
        taskRepository.deleteAll();
        cleanUploadDirectory();
    }

    @Test
    void shouldStoreLoadAndDeleteAttachment() throws Exception {
        Task task = taskRepository.save(task("Task with attachment"));
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "document.txt",
                "text/plain",
                "service test".getBytes()
        );

        TaskAttachment attachment = attachmentService.storeAttachment(task.getId(), file);

        assertEquals("document.txt", attachment.getFileName());
        assertFalse(attachment.getStoredFileName().isBlank());
        assertTrue(Files.exists(Path.of("target/test-service-uploads")
                .resolve(attachment.getStoredFileName())));
        try (InputStream inputStream =
                     attachmentService.loadAsResource(attachment.getId()).getInputStream()) {
            assertEquals("service test", new String(inputStream.readAllBytes()));
        }

        attachmentService.deleteAttachment(attachment.getId());
        assertFalse(Files.exists(Path.of("target/test-service-uploads")
                .resolve(attachment.getStoredFileName())));
    }

    @Test
    void shouldThrowForMissingAttachment() {
        assertThrows(AttachmentNotFoundException.class,
                () -> attachmentService.getAttachment(999L));
    }

    @Test
    void shouldRejectEmptyFile() {
        Task task = taskRepository.save(task("Task with empty file"));
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "empty.txt",
                "text/plain",
                new byte[0]
        );

        assertThrows(IllegalArgumentException.class,
                () -> attachmentService.storeAttachment(task.getId(), file));
    }

    private void cleanUploadDirectory() throws IOException {
        Path uploadDir = Path.of("target/test-service-uploads");
        Files.createDirectories(uploadDir);
        try (Stream<Path> files = Files.list(uploadDir)) {
            files.forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException ignored) {
                }
            });
        }
    }

    private Task task(String title) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription("Description");
        task.setCompleted(false);
        task.setDueDate(LocalDate.now().plusDays(1));
        task.setPriority(Priority.MEDIUM);
        task.setTags(new LinkedHashSet<>());
        return task;
    }
}
