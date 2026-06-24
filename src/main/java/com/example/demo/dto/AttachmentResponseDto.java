package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Attachment metadata returned to API clients")
public record AttachmentResponseDto(
        @Schema(description = "Attachment identifier", example = "1")
        Long id,

        @Schema(description = "Original file name", example = "notes.pdf")
        String fileName,

        @Schema(description = "File size in bytes", example = "2048")
        long size,

        @Schema(description = "Upload timestamp", example = "2026-05-04T10:15:30")
        LocalDateTime uploadedAt
) {
}
