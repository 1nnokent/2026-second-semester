package com.example.demo.controller;

import com.example.demo.dto.ErrorResponse;
import com.example.demo.dto.ViewPreferenceResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Preferences", description = "Cookie-based client preferences")
@RestController
@RequestMapping("/api/preferences")
public class PreferencesController {

    private static final String COOKIE_NAME = "viewPreference";
    private static final String DEFAULT_MODE = "detailed";

    @Operation(summary = "Read the current view preference cookie")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Preference returned")
    })
    @GetMapping("/view")
    public ResponseEntity<ViewPreferenceResponseDto> getViewPreference(
            @CookieValue(value = COOKIE_NAME, required = false) String mode,
            HttpServletResponse response) {
        String resolvedMode = mode == null ? DEFAULT_MODE : normalizeMode(mode);
        if (mode == null) {
            addCookie(response, resolvedMode);
        }
        return ResponseEntity.ok(new ViewPreferenceResponseDto(resolvedMode));
    }

    @Operation(summary = "Update the current view preference cookie")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Preference updated"),
            @ApiResponse(responseCode = "400", description = "Invalid mode", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/view")
    public ResponseEntity<ViewPreferenceResponseDto> setViewPreference(
            @RequestParam String mode,
            HttpServletResponse response) {
        String normalizedMode = normalizeMode(mode);
        addCookie(response, normalizedMode);
        return ResponseEntity.ok(new ViewPreferenceResponseDto(normalizedMode));
    }

    private String normalizeMode(String mode) {
        String normalized = mode == null ? DEFAULT_MODE : mode.trim().toLowerCase();
        if (!"compact".equals(normalized) && !"detailed".equals(normalized)) {
            throw new IllegalArgumentException("mode must be either compact or detailed");
        }
        return normalized;
    }

    private void addCookie(HttpServletResponse response, String mode) {
        Cookie cookie = new Cookie(COOKIE_NAME, mode);
        cookie.setHttpOnly(false);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 30);
        response.addCookie(cookie);
    }
}
