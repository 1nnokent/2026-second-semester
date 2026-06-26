package com.example.demo.dto.security;

import java.util.List;

public record DocsResponse(
        String message,
        List<String> references
) {
}
