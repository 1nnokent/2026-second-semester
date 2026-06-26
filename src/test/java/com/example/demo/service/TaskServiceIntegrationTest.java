package com.example.demo.service;

import com.example.demo.dto.TaskPriorityCountDto;
import com.example.demo.exception.TaskBulkOperationException;
import com.example.demo.model.Priority;
import com.example.demo.model.Task;
import com.example.demo.model.TaskAttachment;
import com.example.demo.repository.TaskAttachmentRepository;
import com.example.demo.repository.TaskRepository;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class TaskServiceIntegrationTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskStatisticsJdbcService taskStatisticsJdbcService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskAttachmentRepository attachmentRepository;

    @BeforeEach
    void setUp() {
        attachmentRepository.deleteAll();
        taskRepository.deleteAll();
    }

    @Test
    void shouldRollbackBulkCompletionWhenTaskIsMissing() {
        Task existing = taskRepository.save(task("Existing", false, Priority.MEDIUM));

        assertThrows(TaskBulkOperationException.class,
                () -> taskService.bulkCompleteTasks(List.of(existing.getId(), 9999L)));

        Task reloaded = taskRepository.findById(existing.getId()).orElseThrow();
        assertFalse(reloaded.isCompleted());
    }

    @Test
    void shouldCompleteAllTasksInBulkWhenIdsExist() {
        Task first = taskRepository.save(task("First", false, Priority.HIGH));
        Task second = taskRepository.save(task("Second", false, Priority.LOW));

        taskService.bulkCompleteTasks(List.of(first.getId(), second.getId()));

        assertTrue(taskRepository.findById(first.getId()).orElseThrow().isCompleted());
        assertTrue(taskRepository.findById(second.getId()).orElseThrow().isCompleted());
    }

    @Test
    void shouldLoadTasksWithAttachments() {
        Task task = taskRepository.save(task("Task with file", false, Priority.HIGH));
        TaskAttachment attachment = new TaskAttachment();
        attachment.setTask(task);
        attachment.setFileName("diagram.png");
        attachment.setStoredFileName("stored-diagram.png");
        attachment.setContentType("image/png");
        attachment.setSize(1024L);
        attachmentRepository.save(attachment);

        List<Task> tasks = taskService.getAllTasksWithAttachments();

        assertEquals(1, tasks.size());
        assertEquals(1, tasks.getFirst().getAttachments().size());
    }

    @Test
    void shouldReturnTaskStatisticsByPriorityUsingJdbc() {
        taskRepository.save(task("High one", false, Priority.HIGH));
        taskRepository.save(task("High two", true, Priority.HIGH));
        taskRepository.save(task("Low one", false, Priority.LOW));

        List<TaskPriorityCountDto> statistics = taskStatisticsJdbcService.getTasksCountByPriority();

        assertTrue(statistics.stream()
                .anyMatch(item -> item.priority() == Priority.HIGH && item.count() == 2L));
        assertTrue(statistics.stream()
                .anyMatch(item -> item.priority() == Priority.LOW && item.count() == 1L));
    }

    private Task task(String title, boolean completed, Priority priority) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription("Description for " + title);
        task.setCompleted(completed);
        task.setDueDate(LocalDate.now().plusDays(2));
        task.setPriority(priority);
        task.setTags(new LinkedHashSet<>(Set.of("integration")));
        return task;
    }
}
