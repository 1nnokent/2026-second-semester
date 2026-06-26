package com.example.demo.service;

import com.example.demo.client.CreatedExternalTask;
import com.example.demo.client.ExternalTasksClient;
import com.example.demo.dto.external.ExternalTaskRequest;
import com.example.demo.dto.external.ExternalTaskResponse;
import com.example.demo.dto.gateway.CreatedGatewayTask;
import com.example.demo.dto.gateway.GatewayOperationResponse;
import com.example.demo.dto.gateway.GatewayTaskResponse;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TasksGatewayService {

    private final ExternalTasksClient externalTasksClient;

    public TasksGatewayService(ExternalTasksClient externalTasksClient) {
        this.externalTasksClient = externalTasksClient;
    }

    public CreatedGatewayTask createTask(ExternalTaskRequest request) {
        CreatedExternalTask createdTask = externalTasksClient.createTask(request);
        return new CreatedGatewayTask(toGatewayResponse(createdTask.task(), false, "external",
                null), createdTask.location());
    }

    public GatewayTaskResponse getTask(Long id) {
        return toGatewayResponse(externalTasksClient.getTask(id), false, "external", null);
    }

    public List<GatewayTaskResponse> getTasks(Boolean completed, Integer limit) {
        return externalTasksClient.getTasks(completed, limit).stream()
                .map(task -> toGatewayResponse(task, false, "external", null))
                .toList();
    }

    public GatewayOperationResponse deleteTask(Long id) {
        externalTasksClient.deleteTask(id);
        return new GatewayOperationResponse("Task deleted in external API", false, "external");
    }

    private GatewayTaskResponse toGatewayResponse(ExternalTaskResponse task, boolean degraded,
            String source, String message) {
        return new GatewayTaskResponse(task.id(), task.title(), task.description(),
                task.completed(), degraded, source, message);
    }
}
