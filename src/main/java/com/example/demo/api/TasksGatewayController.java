package com.example.demo.api;

import com.example.demo.dto.external.ExternalTaskRequest;
import com.example.demo.dto.gateway.CreatedGatewayTask;
import com.example.demo.dto.gateway.GatewayOperationResponse;
import com.example.demo.dto.gateway.GatewayTaskResponse;
import com.example.demo.service.TasksGatewayService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/tasks")
public class TasksGatewayController {

    private final TasksGatewayService tasksGatewayService;

    public TasksGatewayController(TasksGatewayService tasksGatewayService) {
        this.tasksGatewayService = tasksGatewayService;
    }

    @PostMapping
    public ResponseEntity<GatewayTaskResponse> createTask(
            @Valid @RequestBody ExternalTaskRequest request) {
        CreatedGatewayTask createdTask = tasksGatewayService.createTask(request);
        if (createdTask.task().degraded() || createdTask.task().id() == null) {
            return ResponseEntity.ok(createdTask.task());
        }

        URI internalLocation = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdTask.task().id())
                .toUri();

        ResponseEntity.BodyBuilder response = ResponseEntity.created(internalLocation);
        if (createdTask.externalLocation() != null) {
            response.header("X-External-Location", createdTask.externalLocation().toString());
        }
        return response.body(createdTask.task());
    }

    @GetMapping("/{id}")
    public GatewayTaskResponse getTask(@PathVariable Long id) {
        return tasksGatewayService.getTask(id);
    }

    @GetMapping
    public List<GatewayTaskResponse> getTasks(
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) Integer limit) {
        return tasksGatewayService.getTasks(completed, limit);
    }

    @GetMapping("/unstable")
    public GatewayOperationResponse unstable(@RequestParam String mode) {
        return tasksGatewayService.callUnstable(mode);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        GatewayOperationResponse response = tasksGatewayService.deleteTask(id);
        if (response.degraded()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.noContent().build();
    }
}
