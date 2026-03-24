package com.example.demo.dto;

import com.example.demo.model.Priority;
import com.example.demo.validation.OnCreate;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

public record TaskCreateDto (
        @NotNull(groups = OnCreate.class)
        @Size(min=3, max=100, groups = OnCreate.class)
        String title,

        @Size(max=500, groups = OnCreate.class)
        String description,

        @FutureOrPresent(groups = OnCreate.class)
        LocalDateTime dueDate,

        @NotNull(groups = OnCreate.class)
        Priority priority,

        @Size(max=5, groups = OnCreate.class)
        Set<String> tags
) { }
