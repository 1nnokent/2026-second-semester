package com.example.demo.dto;

import com.example.demo.model.Priority;
import com.example.demo.validation.OnCreate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

@Schema(description = "Payload for creating a new task")
public record TaskCreateDto(
        @Schema(description = "Short task title", example = "Prepare seminar slides")
        @NotBlank(groups = OnCreate.class)
        @Size(min = 3, max = 100, groups = OnCreate.class)
        String title,

        @Schema(description = "Detailed task description", example = "Collect references and build the final deck")
        @Size(max = 500, groups = OnCreate.class)
        String description,

        @Schema(description = "Date when the task should be completed", example = "2026-05-10")
        @FutureOrPresent(groups = OnCreate.class)
        LocalDate dueDate,

        @Schema(description = "Task priority", example = "HIGH")
        @NotNull(groups = OnCreate.class)
        Priority priority,

        @Schema(description = "Task tags", example = "[\"study\", \"slides\"]")
        @Size(max = 5, groups = OnCreate.class)
        Set<String> tags
) {
}
