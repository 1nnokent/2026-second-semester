package com.example.demo.controller;

import com.example.demo.dto.ErrorResponse;
import com.example.demo.dto.TaskResponseDto;
import com.example.demo.mapper.TaskMapper;
import com.example.demo.service.FavoritesService;
import com.example.demo.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Favorites", description = "Favorite tasks stored in the HTTP session")
@RestController
@RequestMapping("/api/favorites")
public class FavoritesController {

    private final FavoritesService favoritesService;
    private final TaskService taskService;
    private final TaskMapper taskMapper;

    public FavoritesController(FavoritesService favoritesService, TaskService taskService,
            TaskMapper taskMapper) {
        this.favoritesService = favoritesService;
        this.taskService = taskService;
        this.taskMapper = taskMapper;
    }

    @Operation(summary = "Add a task to favorites")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task added to favorites"),
            @ApiResponse(responseCode = "404", description = "Task not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{taskId}")
    public ResponseEntity<Void> addToFavorites(@PathVariable int taskId, HttpSession session) {
        taskService.getTask(taskId);
        favoritesService.addToFavorites(taskId, session);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Remove a task from favorites")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Task removed from favorites")
    })
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> removeFromFavorites(@PathVariable int taskId, HttpSession session) {
        favoritesService.removeFromFavorites(taskId, session);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get favorite tasks from the session")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Favorite tasks returned")
    })
    @GetMapping
    public ResponseEntity<List<TaskResponseDto>> getFavorites(HttpSession session) {
        List<TaskResponseDto> favorites = taskService
                .getTasksByIds(favoritesService.getFavoriteTaskIds(session))
                .stream()
                .map(taskMapper::toResponseDto)
                .toList();
        return ResponseEntity.ok(favorites);
    }
}
