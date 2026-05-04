package com.example.demo.dto;

import com.example.demo.model.Priority;
import com.example.demo.validation.OnUpdate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

@Schema(description = "Payload for partial task update")
public record TaskUpdateDto(
        @Schema(description = "Updated task title", example = "Prepare updated seminar slides")
        @Pattern(regexp = ".*\\S.*", groups = OnUpdate.class)
        @Size(min = 3, max = 100, groups = OnUpdate.class)
        String title,

        @Schema(description = "Updated task description", example = "Update charts and add summary slide")
        @Size(max = 500, groups = OnUpdate.class)
        String description,

        @Schema(description = "Task completion flag", example = "true")
        Boolean completed,

        @Schema(description = "Updated due date", example = "2026-05-12")
        @FutureOrPresent(groups = OnUpdate.class)
        LocalDate dueDate,

        @Schema(description = "Updated task priority", example = "MEDIUM")
        Priority priority,

        @Schema(description = "Updated task tags", example = "[\"study\", \"urgent\"]")
        @Size(max = 5, groups = OnUpdate.class)
        Set<String> tags
) {
}
