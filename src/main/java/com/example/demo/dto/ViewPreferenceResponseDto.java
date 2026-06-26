package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Current preferred task list view mode")
public record ViewPreferenceResponseDto(
        @Schema(description = "View mode", allowableValues = {"compact", "detailed"}, example = "detailed")
        String mode
) {
}
