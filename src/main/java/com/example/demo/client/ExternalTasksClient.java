package com.example.demo.client;

import com.example.demo.config.ExternalApiProperties;
import com.example.demo.dto.external.ExternalTaskRequest;
import com.example.demo.dto.external.ExternalTaskResponse;
import com.example.demo.exception.ExternalApiException;
import com.example.demo.exception.TaskNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class ExternalTasksClient {

    private static final Logger log = LoggerFactory.getLogger(ExternalTasksClient.class);
    private static final int BODY_LOG_LIMIT = 240;
    private static final ParameterizedTypeReference<List<ExternalTaskResponse>> TASK_LIST_TYPE =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final ExternalApiProperties externalApiProperties;
    private final ObjectProvider<ServletWebServerApplicationContext> webServerContextProvider;

    public ExternalTasksClient(RestClient externalTasksRestClient, ObjectMapper objectMapper,
            ExternalApiProperties externalApiProperties,
            ObjectProvider<ServletWebServerApplicationContext> webServerContextProvider) {
        this.restClient = externalTasksRestClient;
        this.objectMapper = objectMapper;
        this.externalApiProperties = externalApiProperties;
        this.webServerContextProvider = webServerContextProvider;
    }

    public CreatedExternalTask createTask(ExternalTaskRequest request) {
        ResponseEntity<ExternalTaskResponse> response = applyErrorHandling(
                restClient.post()
                        .uri(tasksUri())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .body(request),
                "create task")
                .toEntity(ExternalTaskResponse.class);

        ExternalTaskResponse task = requireBody(response.getBody(), "create task");
        return new CreatedExternalTask(task, response.getHeaders().getLocation());
    }

    public ExternalTaskResponse getTask(Long id) {
        ResponseEntity<ExternalTaskResponse> response = applyErrorHandling(
                restClient.get()
                        .uri(taskUri(id))
                        .accept(MediaType.APPLICATION_JSON),
                "get task " + id)
                .toEntity(ExternalTaskResponse.class);

        return requireBody(response.getBody(), "get task " + id);
    }

    public List<ExternalTaskResponse> getTasks(Boolean completed, Integer limit) {
        List<ExternalTaskResponse> tasks = applyErrorHandling(
                restClient.get()
                        .uri(tasksUri(completed, limit))
                        .accept(MediaType.APPLICATION_JSON),
                "list tasks")
                .body(TASK_LIST_TYPE);

        return tasks == null ? List.of() : tasks;
    }

    public void deleteTask(Long id) {
        applyErrorHandling(
                restClient.delete()
                        .uri(taskUri(id))
                        .accept(MediaType.APPLICATION_JSON),
                "delete task " + id)
                .toBodilessEntity();
    }

    public void callUnstable(String mode) {
        applyErrorHandling(
                restClient.get()
                        .uri(unstableUri(mode))
                        .accept(MediaType.ALL),
                "probe unstable mode " + mode)
                .toBodilessEntity();
    }

    private RestClient.ResponseSpec applyErrorHandling(RestClient.RequestHeadersSpec<?> requestSpec,
            String operation) {
        return requestSpec.retrieve()
                .onStatus(status -> status.value() == HttpStatus.NOT_FOUND.value(),
                        (request, response) -> {
                            throw mapTaskNotFound(response);
                        })
                .onStatus(status -> status.value() == HttpStatus.TOO_MANY_REQUESTS.value(),
                        (request, response) -> {
                            throw mapExternalApiException(response,
                                    "External API rate limited " + operation);
                        })
                .onStatus(HttpStatusCode::is5xxServerError,
                        (request, response) -> {
                            throw mapExternalApiException(response,
                                    "External API failed to " + operation);
                        })
                .onStatus(HttpStatusCode::is4xxClientError,
                        (request, response) -> {
                            throw mapExternalApiException(response,
                                    "External API rejected request to " + operation);
                        });
    }

    private TaskNotFoundException mapTaskNotFound(
            org.springframework.http.client.ClientHttpResponse response) throws IOException {
        String body = readBody(response);
        String detail = extractProblemDetail(body);
        return new TaskNotFoundException(
                StringUtils.hasText(detail) ? detail : "External task was not found");
    }

    private ExternalApiException mapExternalApiException(
            org.springframework.http.client.ClientHttpResponse response, String fallbackMessage)
            throws IOException {
        String body = readBody(response);
        MediaType contentType = response.getHeaders().getContentType();
        if (contentType != null && MediaType.TEXT_HTML.isCompatibleWith(contentType)) {
            log.warn("External API returned unexpected contentType={} body={}",
                    contentType, truncate(body));
        }

        String detail = extractProblemDetail(body);
        String message = StringUtils.hasText(detail) ? detail : fallbackMessage;
        return new ExternalApiException(message, response.getStatusCode().value());
    }

    private String readBody(org.springframework.http.client.ClientHttpResponse response)
            throws IOException {
        return StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);
    }

    private String extractProblemDetail(String body) {
        if (!StringUtils.hasText(body)) {
            return null;
        }

        try {
            JsonNode jsonNode = objectMapper.readTree(body);
            return jsonNode.path("detail").asText(null);
        } catch (JsonProcessingException exception) {
            return null;
        }
    }

    private String truncate(String body) {
        if (body == null || body.length() <= BODY_LOG_LIMIT) {
            return body;
        }
        return body.substring(0, BODY_LOG_LIMIT) + "...";
    }

    private ExternalTaskResponse requireBody(ExternalTaskResponse body, String operation) {
        if (body == null) {
            throw new ExternalApiException("External API returned empty body for " + operation,
                    HttpStatus.BAD_GATEWAY.value());
        }
        return body;
    }

    private URI tasksUri() {
        return UriComponentsBuilder.fromUriString(resolveBaseUrl())
                .path("/tasks")
                .build(true)
                .toUri();
    }

    private URI taskUri(Long id) {
        return UriComponentsBuilder.fromUriString(resolveBaseUrl())
                .path("/tasks/{id}")
                .buildAndExpand(id)
                .toUri();
    }

    private URI tasksUri(Boolean completed, Integer limit) {
        return UriComponentsBuilder.fromUriString(resolveBaseUrl())
                .path("/tasks")
                .queryParamIfPresent("completed", Optional.ofNullable(completed))
                .queryParamIfPresent("limit", Optional.ofNullable(limit))
                .build(true)
                .toUri();
    }

    private URI unstableUri(String mode) {
        return UriComponentsBuilder.fromUriString(resolveBaseUrl())
                .path("/unstable")
                .queryParam("mode", mode)
                .build(true)
                .toUri();
    }

    private String resolveBaseUrl() {
        if (StringUtils.hasText(externalApiProperties.getBaseUrl())) {
            return trimTrailingSlash(externalApiProperties.getBaseUrl());
        }

        ServletWebServerApplicationContext context = webServerContextProvider.getIfAvailable();
        if (context != null && context.getWebServer() != null) {
            return "http://localhost:" + context.getWebServer().getPort() + "/external/v1";
        }

        return "http://localhost:8080/external/v1";
    }

    private String trimTrailingSlash(String baseUrl) {
        return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }
}
