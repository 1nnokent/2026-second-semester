package com.example.demo.dto.external;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ExternalTaskRequest(
        @NotBlank(message = "title must not be blank")
        @Size(max = 100, message = "title must be at most 100 characters")
        String title,

        @Size(max = 500, message = "description must be at most 500 characters")
        String description,

        boolean completed
) {
}
