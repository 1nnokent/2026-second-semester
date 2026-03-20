package com.example.demo.dto;

import com.example.demo.model.Priority;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public record TaskResponseDto (int id, String title, String description, boolean completed, LocalDateTime createdAt, LocalDate dueTime, Priority priority, Set<String> tags) { }
