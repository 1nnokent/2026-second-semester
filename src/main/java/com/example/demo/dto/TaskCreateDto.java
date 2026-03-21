package com.example.demo.dto;

import com.example.demo.model.Priority;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

public record TaskCreateDto (
        @NotNull
        @Size(min=3, max=100)
        String title,
        @Size(max=500)
        String description,
        @FutureOrPresent
        LocalDate dueTime,
        @NotNull
        Priority priority,
        @Size(max=5)
        Set<String> tags) { }
