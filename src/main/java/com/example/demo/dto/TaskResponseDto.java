package com.example.demo.dto;

import com.example.demo.model.Priority;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Schema(description = "Task representation returned to API clients")
public record TaskResponseDto(
        @Schema(description = "Task identifier", example = "1")
        Long id,

        @Schema(description = "Short task title", example = "Prepare seminar slides")
        String title,

        @Schema(description = "Detailed task description", example = "Collect references and build the final deck")
        String description,

        @Schema(description = "Task completion flag", example = "false")
        boolean completed,

        @Schema(description = "Date and time when the task was created", example = "2026-05-04T10:15:30")
        LocalDateTime createdAt,

        @Schema(description = "Date when the task should be completed", example = "2026-05-10")
        LocalDate dueDate,

        @Schema(description = "Task priority", example = "HIGH")
        Priority priority,

        @Schema(description = "Task tags", example = "[\"study\", \"slides\"]")
        Set<String> tags
) {
}
