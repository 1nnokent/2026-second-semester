package com.example.demo.dto;

import com.example.demo.model.Priority;
import java.time.LocalDate;
import java.util.Set;

public record TaskUpdateDto(
        String title,
        String description,
        boolean completed,
        LocalDate dueTime,
        Priority priority,
        Set<String> tags) { }
