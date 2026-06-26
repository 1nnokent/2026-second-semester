package com.example.demo.external;

import com.example.demo.dto.external.ExternalTaskRequest;
import com.example.demo.dto.external.ExternalTaskResponse;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/external/v1")
public class ExternalApiController {

    private static final Duration UNSTABLE_TIMEOUT_DELAY = Duration.ofSeconds(5);

    private final Map<Long, ExternalTaskResponse> tasks = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

    @PostMapping("/tasks")
    public ResponseEntity<ExternalTaskResponse> createTask(
            @Valid @RequestBody ExternalTaskRequest request) {
        long id = sequence.incrementAndGet();
        ExternalTaskResponse response = new ExternalTaskResponse(id, request.title(),
                request.description(), request.completed());
        tasks.put(id, response);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<?> getTask(@PathVariable Long id) {
        ExternalTaskResponse task = tasks.get(id);
        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(externalTaskNotFound(id));
        }
        return ResponseEntity.ok(task);
    }

    @GetMapping("/tasks")
    public List<ExternalTaskResponse> getTasks(
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) Integer limit) {
        return tasks.values().stream()
                .sorted(Comparator.comparing(ExternalTaskResponse::id))
                .filter(task -> completed == null || task.completed() == completed)
                .limit(limit == null ? Long.MAX_VALUE : Math.max(limit, 0))
                .toList();
    }

    @PutMapping("/tasks/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id,
            @Valid @RequestBody ExternalTaskRequest request) {
        if (!tasks.containsKey(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(externalTaskNotFound(id));
        }
        ExternalTaskResponse response = new ExternalTaskResponse(id, request.title(),
                request.description(), request.completed());
        tasks.put(id, response);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        ExternalTaskResponse removedTask = tasks.remove(id);
        if (removedTask == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(externalTaskNotFound(id));
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/unstable")
    public ResponseEntity<?> unstable(@RequestParam String mode) {
        return switch (mode) {
            case "timeout" -> sleepAndReturn();
            case "500" -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(problem(HttpStatus.INTERNAL_SERVER_ERROR,
                            "External API failed intentionally"));
            case "429" -> ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .header(HttpHeaders.RETRY_AFTER, "2")
                    .body(problem(HttpStatus.TOO_MANY_REQUESTS,
                            "External API rate limit reached intentionally"));
            case "html" -> ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .contentType(MediaType.TEXT_HTML)
                    .body("<html><body><h1>Bad Gateway</h1><p>HTML payload on purpose</p></body></html>");
            default -> ResponseEntity.badRequest()
                    .body(problem(HttpStatus.BAD_REQUEST, "Unsupported mode: " + mode));
        };
    }

    private ResponseEntity<ProblemDetail> sleepAndReturn() {
        try {
            Thread.sleep(UNSTABLE_TIMEOUT_DELAY.toMillis());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(problem(HttpStatus.INTERNAL_SERVER_ERROR, "Sleep was interrupted"));
        }

        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                .body(problem(HttpStatus.GATEWAY_TIMEOUT, "External API simulated timeout"));
    }

    private ProblemDetail externalTaskNotFound(Long id) {
        ProblemDetail problemDetail = problem(HttpStatus.NOT_FOUND,
                "External task with id " + id + " was not found");
        problemDetail.setProperty("taskId", id);
        return problemDetail;
    }

    private ProblemDetail problem(HttpStatus status, String detail) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(status.getReasonPhrase());
        problemDetail.setType(URI.create("https://example.com/problems/" + status.value()));
        return problemDetail;
    }
}
