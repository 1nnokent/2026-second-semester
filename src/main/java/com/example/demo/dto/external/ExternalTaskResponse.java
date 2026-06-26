package com.example.demo.dto.external;

public record ExternalTaskResponse(
        Long id,
        String title,
        String description,
        boolean completed
) {
}
