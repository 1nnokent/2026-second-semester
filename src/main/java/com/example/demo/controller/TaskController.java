package com.example.demo.controller;

import com.example.demo.dto.TaskCreateDto;
import com.example.demo.dto.TaskResponseDto;
import com.example.demo.dto.TaskUpdateDto;
import com.example.demo.mapper.TaskMapper;
import com.example.demo.model.Task;
import com.example.demo.service.TaskService;
import com.example.demo.validation.DueDateNotBeforeCreation;
import com.example.demo.validation.OnCreate;
import com.example.demo.validation.OnUpdate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Tasks", description = "Task management endpoints")
@Validated
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final TaskMapper taskMapper;

    public TaskController(TaskService taskService, TaskMapper taskMapper) {
        this.taskService = taskService;
        this.taskMapper = taskMapper;
    }

    @Operation(summary = "Get all tasks")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tasks returned successfully")
    })
    @GetMapping
    public ResponseEntity<List<TaskResponseDto>> getAllTasks() {
        List<TaskResponseDto> tasks = taskService.getAllTasks().stream()
                .map(taskMapper::toResponseDto)
                .toList();

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(taskService.getTaskCount()))
                .body(tasks);
    }

    @Operation(summary = "Get a task by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task found"),
            @ApiResponse(responseCode = "404", description = "Task not found", content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDto> getTask(@PathVariable int id) {
        return ResponseEntity.ok(taskMapper.toResponseDto(taskService.getTask(id)));
    }

    @Operation(summary = "Create a new task")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Task created"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<TaskResponseDto> createTask(
            @RequestBody @Validated(OnCreate.class) TaskCreateDto taskCreateDto) {
        Task createdTask = taskService.addTask(taskMapper.toEntity(taskCreateDto));
        return ResponseEntity.created(URI.create("/api/tasks/" + createdTask.getId()))
                .body(taskMapper.toResponseDto(createdTask));
    }

    @Operation(summary = "Update an existing task")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Task not found", content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    @DueDateNotBeforeCreation
    public ResponseEntity<TaskResponseDto> updateTask(
            @PathVariable int id,
            @RequestBody @Validated(OnUpdate.class) TaskUpdateDto taskUpdateDto) {
        Task task = taskService.getTask(id);
        Task updatedTask = taskMapper.updateEntity(taskUpdateDto, task);
        updatedTask = taskService.update(id, updatedTask);
        return ResponseEntity.ok(taskMapper.toResponseDto(updatedTask));
    }

    @Operation(summary = "Delete a task")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Task deleted"),
            @ApiResponse(responseCode = "404", description = "Task not found", content = @Content(schema = @Schema(implementation = com.example.demo.dto.ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable int id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
