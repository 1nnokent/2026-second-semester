package com.example.demo.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class ApiVersionHeaderFilter extends OncePerRequestFilter {

    private final String apiVersion;

    public ApiVersionHeaderFilter(@Value("${app.version}") String apiVersion) {
        this.apiVersion = apiVersion;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        response.setHeader("X-API-Version", apiVersion);
        filterChain.doFilter(request, response);
    }
}
