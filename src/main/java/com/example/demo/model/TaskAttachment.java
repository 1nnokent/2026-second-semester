package com.example.demo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Stored metadata for a task attachment")
public record TaskAttachment(
        @Schema(description = "Attachment identifier", example = "1")
        Long id,

        @Schema(description = "Identifier of the task that owns the attachment", example = "5")
        Long taskId,

        @Schema(description = "Original file name", example = "notes.pdf")
        String fileName,

        @Schema(description = "Unique file name used on disk", example = "4d97f4f6f41a45a7a0d7f3db2a6ff729.pdf")
        String storedFileName,

        @Schema(description = "MIME type", example = "application/pdf")
        String contentType,

        @Schema(description = "File size in bytes", example = "2048")
        long size,

        @Schema(description = "Upload timestamp", example = "2026-05-04T10:15:30")
        LocalDateTime uploadedAt
) {
}
