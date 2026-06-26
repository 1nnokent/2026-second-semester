package com.example.demo.service;

import com.example.demo.client.CreatedExternalTask;
import com.example.demo.client.ExternalTasksClient;
import com.example.demo.dto.external.ExternalTaskRequest;
import com.example.demo.dto.external.ExternalTaskResponse;
import com.example.demo.dto.gateway.CreatedGatewayTask;
import com.example.demo.dto.gateway.GatewayOperationResponse;
import com.example.demo.dto.gateway.GatewayTaskResponse;
import com.example.demo.exception.TaskNotFoundException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class TasksGatewayService {

    private final ExternalTasksClient externalTasksClient;

    public TasksGatewayService(ExternalTasksClient externalTasksClient) {
        this.externalTasksClient = externalTasksClient;
    }

    @RateLimiter(name = "externalApi")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "createTaskFallback")
    public CreatedGatewayTask createTask(ExternalTaskRequest request) {
        CreatedExternalTask createdTask = externalTasksClient.createTask(request);
        return new CreatedGatewayTask(toGatewayResponse(createdTask.task(), false, "external",
                null), createdTask.location());
    }

    @RateLimiter(name = "externalApi")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "getTaskFallback")
    public GatewayTaskResponse getTask(Long id) {
        return toGatewayResponse(externalTasksClient.getTask(id), false, "external", null);
    }

    @RateLimiter(name = "externalApi")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "getTasksFallback")
    public List<GatewayTaskResponse> getTasks(Boolean completed, Integer limit) {
        return externalTasksClient.getTasks(completed, limit).stream()
                .map(task -> toGatewayResponse(task, false, "external", null))
                .toList();
    }

    @RateLimiter(name = "externalApi")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "deleteTaskFallback")
    public GatewayOperationResponse deleteTask(Long id) {
        externalTasksClient.deleteTask(id);
        return new GatewayOperationResponse("Task deleted in external API", false, "external");
    }

    @RateLimiter(name = "externalApi")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "callUnstableFallback")
    public GatewayOperationResponse callUnstable(String mode) {
        externalTasksClient.callUnstable(mode);
        return new GatewayOperationResponse(
                "External unstable endpoint responded without triggering fallback",
                false,
                "external");
    }

    private CreatedGatewayTask createTaskFallback(ExternalTaskRequest request, Throwable throwable) {
        Throwable cause = ensureFallbackable(throwable);
        return new CreatedGatewayTask(
                new GatewayTaskResponse(
                        null,
                        request.title(),
                        request.description(),
                        request.completed(),
                        true,
                        "fallback",
                        fallbackMessage(cause)),
                null);
    }

    private GatewayTaskResponse getTaskFallback(Long id, Throwable throwable) {
        Throwable cause = ensureFallbackable(throwable);
        return new GatewayTaskResponse(
                id,
                "fallback-task-" + id,
                fallbackMessage(cause),
                false,
                true,
                "fallback",
                fallbackMessage(cause));
    }

    private List<GatewayTaskResponse> getTasksFallback(Boolean completed, Integer limit,
            Throwable throwable) {
        ensureFallbackable(throwable);
        return List.of();
    }

    private GatewayOperationResponse deleteTaskFallback(Long id, Throwable throwable) {
        Throwable cause = ensureFallbackable(throwable);
        return new GatewayOperationResponse(
                "Delete was skipped because external API is unavailable: " + fallbackMessage(cause),
                true,
                "fallback");
    }

    private GatewayOperationResponse callUnstableFallback(String mode, Throwable throwable) {
        Throwable cause = ensureFallbackable(throwable);
        return new GatewayOperationResponse(
                "Graceful fallback for unstable mode " + mode + ": " + fallbackMessage(cause),
                true,
                "fallback");
    }

    private Throwable ensureFallbackable(Throwable throwable) {
        Throwable cause = unwrap(throwable);
        if (cause instanceof TaskNotFoundException taskNotFoundException) {
            throw taskNotFoundException;
        }
        if (cause instanceof RequestNotPermitted requestNotPermitted) {
            throw requestNotPermitted;
        }
        return cause;
    }

    private Throwable unwrap(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }
        return current;
    }

    private String fallbackMessage(Throwable throwable) {
        if (StringUtils.hasText(throwable.getMessage())) {
            return throwable.getMessage();
        }
        return throwable.getClass().getSimpleName();
    }

    private GatewayTaskResponse toGatewayResponse(ExternalTaskResponse task, boolean degraded,
            String source, String message) {
        return new GatewayTaskResponse(task.id(), task.title(), task.description(),
                task.completed(), degraded, source, message);
    }
}
