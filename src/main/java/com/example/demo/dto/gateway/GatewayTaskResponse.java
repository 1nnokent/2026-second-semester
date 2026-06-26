package com.example.demo.dto.gateway;

public record GatewayTaskResponse(
        Long id,
        String title,
        String description,
        boolean completed,
        boolean degraded,
        String source,
        String message
) {
}
