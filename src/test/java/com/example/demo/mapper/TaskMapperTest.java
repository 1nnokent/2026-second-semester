package com.example.demo.mapper;

import com.example.demo.dto.TaskCreateDto;
import com.example.demo.dto.TaskUpdateDto;
import com.example.demo.model.Priority;
import com.example.demo.model.Task;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskMapperTest {

    private final TaskMapper taskMapper = Mappers.getMapper(TaskMapper.class);

    @Test
    void shouldMapCreateDtoToEntity() {
        TaskCreateDto dto = new TaskCreateDto(
                "Prepare report",
                "Write final version",
                LocalDate.now().plusDays(2),
                Priority.HIGH,
                Set.of("work")
        );

        Task task = taskMapper.toEntity(dto);

        assertEquals("Prepare report", task.getTitle());
        assertEquals(Priority.HIGH, task.getPriority());
        assertFalse(task.isCompleted());
    }

    @Test
    void shouldUpdateOnlyProvidedFields() {
        Task task = new Task(
                1,
                "Original",
                "Original description",
                false,
                LocalDateTime.now().minusHours(1),
                LocalDate.now().plusDays(1),
                Priority.MEDIUM,
                new LinkedHashSet<>(Set.of("old"))
        );
        TaskUpdateDto dto = new TaskUpdateDto(
                null,
                "Updated description",
                true,
                null,
                Priority.HIGH,
                null
        );

        Task updatedTask = taskMapper.updateEntity(dto, task);

        assertEquals("Original", updatedTask.getTitle());
        assertEquals("Updated description", updatedTask.getDescription());
        assertTrue(updatedTask.isCompleted());
        assertEquals(Priority.HIGH, updatedTask.getPriority());
    }
}
