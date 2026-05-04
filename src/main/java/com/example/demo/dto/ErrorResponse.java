package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.Map;

@Schema(description = "Unified API error payload")
public record ErrorResponse(
        @Schema(description = "Timestamp when the error was produced", example = "2026-05-04T10:15:30Z")
        Instant timestamp,

        @Schema(description = "HTTP status code", example = "400")
        int status,

        @Schema(description = "Short HTTP error label", example = "Bad Request")
        String error,

        @Schema(description = "Detailed error message", example = "Validation failed")
        String message,

        @Schema(description = "Request path", example = "/api/tasks")
        String path,

        @Schema(description = "Additional structured details")
        Map<String, Object> details
) {
}
