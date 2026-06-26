package com.example.demo.dto.gateway;

public record GatewayOperationResponse(
        String message,
        boolean degraded,
        String source
) {
}
