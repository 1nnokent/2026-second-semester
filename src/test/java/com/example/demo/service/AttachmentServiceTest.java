package com.example.demo.service;

import com.example.demo.exception.AttachmentNotFoundException;
import com.example.demo.model.Priority;
import com.example.demo.model.Task;
import com.example.demo.model.TaskAttachment;
import com.example.demo.repository.InMemoryTaskRepository;
import com.example.demo.repository.TaskAttachmentRepository;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AttachmentServiceTest {

    @TempDir
    Path tempDir;

    private InMemoryTaskRepository taskRepository;
    private TaskAttachmentRepository attachmentRepository;
    private AttachmentService attachmentService;

    @BeforeEach
    void setUp() {
        taskRepository = new InMemoryTaskRepository();
        attachmentRepository = new TaskAttachmentRepository();
        TaskService taskService = new TaskService(taskRepository);
        taskService.postConstruct();
        Task task = new Task(
                1,
                "Task with attachment",
                "Description",
                false,
                LocalDateTime.now().minusHours(1),
                LocalDate.now().plusDays(1),
                Priority.MEDIUM,
                new LinkedHashSet<>()
        );
        taskRepository.add(task);
        attachmentService = new AttachmentService(attachmentRepository, taskService, tempDir.toString());
    }

    @Test
    void shouldStoreLoadAndDeleteAttachment() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "document.txt",
                "text/plain",
                "service test".getBytes()
        );

        TaskAttachment attachment = attachmentService.storeAttachment(1L, file);

        assertEquals("document.txt", attachment.fileName());
        assertFalse(attachment.storedFileName().isBlank());
        assertTrue(Files.exists(tempDir.resolve(attachment.storedFileName())));
        try (InputStream inputStream =
                     attachmentService.loadAsResource(attachment.id()).getInputStream()) {
            assertEquals("service test", new String(inputStream.readAllBytes()));
        }

        attachmentService.deleteAttachment(attachment.id());
        assertFalse(Files.exists(tempDir.resolve(attachment.storedFileName())));
    }

    @Test
    void shouldThrowForMissingAttachment() {
        assertThrows(AttachmentNotFoundException.class, () -> attachmentService.getAttachment(999L));
    }

    @Test
    void shouldRejectEmptyFile() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "empty.txt",
                "text/plain",
                new byte[0]
        );

        assertThrows(IllegalArgumentException.class, () -> attachmentService.storeAttachment(1L, file));
    }
}
