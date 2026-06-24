package com.example.demo.repository;

import com.example.demo.model.Priority;
import com.example.demo.model.Task;
import com.example.demo.model.TaskAttachment;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskAttachmentRepository attachmentRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldFindTasksByCompletedAndPriority() {
        taskRepository.save(task("High completed", true, LocalDate.now().plusDays(1), Priority.HIGH));
        taskRepository.save(task("Low completed", true, LocalDate.now().plusDays(1), Priority.LOW));
        taskRepository.save(task("High open", false, LocalDate.now().plusDays(1), Priority.HIGH));

        List<Task> tasks = taskRepository.findByCompletedAndPriority(true, Priority.HIGH);

        assertEquals(1, tasks.size());
        assertEquals("High completed", tasks.getFirst().getTitle());
    }

    @Test
    void shouldFindTasksDueWithinNext7Days() {
        taskRepository.save(task("Soon", false, LocalDate.now().plusDays(3), Priority.MEDIUM));
        taskRepository.save(task("Later", false, LocalDate.now().plusDays(10), Priority.MEDIUM));

        List<Task> tasks = taskRepository.findTasksDueWithinNext7Days(LocalDate.now(),
                LocalDate.now().plusDays(7));

        assertEquals(1, tasks.size());
        assertEquals("Soon", tasks.getFirst().getTitle());
    }

    @Test
    void shouldPersistTaskWithAttachmentAndFetchItWithoutNPlusOne() {
        Task task = taskRepository.save(task("Task with attachment", false,
                LocalDate.now().plusDays(2), Priority.HIGH));

        TaskAttachment attachment = new TaskAttachment();
        attachment.setTask(task);
        attachment.setFileName("brief.pdf");
        attachment.setStoredFileName("stored-brief.pdf");
        attachment.setContentType("application/pdf");
        attachment.setSize(2048L);
        attachmentRepository.save(attachment);

        entityManager.flush();
        entityManager.clear();

        List<Task> tasks = taskRepository.findAllWithAttachments();

        assertEquals(1, tasks.size());
        assertEquals(1, tasks.getFirst().getAttachments().size());
        assertEquals("brief.pdf", tasks.getFirst().getAttachments().getFirst().getFileName());
    }

    @Test
    void shouldFindAttachmentsByTaskId() {
        Task task = taskRepository.save(task("Task attachment query", false,
                LocalDate.now().plusDays(2), Priority.MEDIUM));

        TaskAttachment first = new TaskAttachment();
        first.setTask(task);
        first.setFileName("a.txt");
        first.setStoredFileName("stored-a.txt");
        first.setContentType("text/plain");
        first.setSize(10L);

        TaskAttachment second = new TaskAttachment();
        second.setTask(task);
        second.setFileName("b.txt");
        second.setStoredFileName("stored-b.txt");
        second.setContentType("text/plain");
        second.setSize(20L);

        attachmentRepository.saveAll(List.of(first, second));

        entityManager.flush();
        entityManager.clear();

        List<TaskAttachment> attachments = attachmentRepository.findAllByTask_IdOrderByUploadedAtAsc(
                task.getId());

        assertEquals(2, attachments.size());
        assertTrue(attachments.stream().anyMatch(attachment -> "a.txt".equals(attachment.getFileName())));
        assertTrue(attachments.stream().anyMatch(attachment -> "b.txt".equals(attachment.getFileName())));
    }

    private Task task(String title, boolean completed, LocalDate dueDate, Priority priority) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription("Description for " + title);
        task.setCompleted(completed);
        task.setDueDate(dueDate);
        task.setPriority(priority);
        task.setTags(new LinkedHashSet<>(Set.of("study")));
        return task;
    }
}
