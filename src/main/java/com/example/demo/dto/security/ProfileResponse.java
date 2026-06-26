package com.example.demo.dto.security;

import java.util.List;

public record ProfileResponse(
        String username,
        List<String> authorities
) {
}
