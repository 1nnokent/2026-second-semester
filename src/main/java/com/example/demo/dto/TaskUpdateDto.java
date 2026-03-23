package com.example.demo.dto;

import com.example.demo.model.Priority;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;

public record TaskUpdateDto(
        @Size(min = 3, max = 100, groups = OnUpdate.class)
        String title,

        @Size(max = 500, groups = OnUpdate.class)
        String description,

        Boolean completed,

        @FutureOrPresent(groups = OnUpdate.class)
        LocalDate dueTime,

        Priority priority,

        @Size(max = 5, groups = OnUpdate.class)
        Set<String> tags
) { }
