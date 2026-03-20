package com.example.demo.dto;

import com.example.demo.model.Priority;
import java.time.LocalDate;
import java.util.Set;

public record TaskCreateDto (String title, String description, LocalDate dueTime, Priority priority, Set<String> tags) { }
