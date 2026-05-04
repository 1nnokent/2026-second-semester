package com.example.demo.controller;

import com.example.demo.dto.AttachmentResponseDto;
import com.example.demo.dto.ErrorResponse;
import com.example.demo.model.TaskAttachment;
import com.example.demo.service.AttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Attachments", description = "Task attachment endpoints")
@RestController
@RequestMapping("/api")
public class AttachmentController {

    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @Operation(summary = "Upload an attachment for a task")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Attachment uploaded"),
            @ApiResponse(responseCode = "400", description = "Invalid file", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Task not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/tasks/{taskId}/attachments")
    public ResponseEntity<AttachmentResponseDto> uploadAttachment(
            @PathVariable Long taskId,
            @RequestParam("file") MultipartFile file) throws IOException {
        TaskAttachment attachment = attachmentService.storeAttachment(taskId, file);
        return ResponseEntity.created(URI.create("/api/attachments/" + attachment.id()))
                .body(toResponseDto(attachment));
    }

    @Operation(summary = "Download an attachment by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Attachment returned"),
            @ApiResponse(responseCode = "404", description = "Attachment not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/attachments/{attachmentId}")
    public ResponseEntity<Resource> downloadAttachment(@PathVariable Long attachmentId)
            throws IOException {
        TaskAttachment attachment = attachmentService.getAttachment(attachmentId);
        Resource resource = attachmentService.loadAsResource(attachmentId);

        MediaType mediaType = attachment.contentType() == null
                ? MediaType.APPLICATION_OCTET_STREAM
                : MediaType.parseMediaType(attachment.contentType());

        return ResponseEntity.ok()
                .contentType(mediaType)
                .contentLength(attachment.size())
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(attachment.fileName())
                        .build()
                        .toString())
                .body(resource);
    }

    @Operation(summary = "Delete an attachment by id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Attachment deleted"),
            @ApiResponse(responseCode = "404", description = "Attachment not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/attachments/{attachmentId}")
    public ResponseEntity<Void> deleteAttachment(@PathVariable Long attachmentId) throws IOException {
        attachmentService.deleteAttachment(attachmentId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get attachment metadata for a task")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Attachments returned"),
            @ApiResponse(responseCode = "404", description = "Task not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/tasks/{taskId}/attachments")
    public ResponseEntity<List<AttachmentResponseDto>> getTaskAttachments(@PathVariable Long taskId) {
        List<AttachmentResponseDto> attachments = attachmentService.getAttachmentsByTaskId(taskId)
                .stream()
                .map(this::toResponseDto)
                .toList();
        return ResponseEntity.ok(attachments);
    }

    private AttachmentResponseDto toResponseDto(TaskAttachment attachment) {
        return new AttachmentResponseDto(
                attachment.id(),
                attachment.fileName(),
                attachment.size(),
                attachment.uploadedAt()
        );
    }
}
