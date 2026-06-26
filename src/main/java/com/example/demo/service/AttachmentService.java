package com.example.demo.service;

import com.example.demo.exception.AttachmentNotFoundException;
import com.example.demo.model.Task;
import com.example.demo.model.TaskAttachment;
import com.example.demo.repository.TaskAttachmentRepository;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
public class AttachmentService {

    private final TaskAttachmentRepository attachmentRepository;
    private final TaskService taskService;
    private final Path uploadPath;

    public AttachmentService(TaskAttachmentRepository attachmentRepository,
            TaskService taskService,
            @Value("${app.upload.dir}") String uploadDir) {
        this.attachmentRepository = attachmentRepository;
        this.taskService = taskService;
        this.uploadPath = Path.of(uploadDir).toAbsolutePath().normalize();
    }

    @Transactional(rollbackFor = IOException.class)
    public TaskAttachment storeAttachment(Long taskId, MultipartFile file) throws IOException {
        Task task = taskService.getTask(taskId);
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Attachment file must not be empty");
        }

        Files.createDirectories(uploadPath);

        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = StringUtils.getFilenameExtension(originalFileName);
        String storedFileName = extension == null || extension.isBlank()
                ? UUID.randomUUID().toString()
                : UUID.randomUUID() + "." + extension;

        Path targetFile = uploadPath.resolve(storedFileName);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, targetFile, StandardCopyOption.REPLACE_EXISTING);
        }

        try {
            TaskAttachment attachment = new TaskAttachment();
            attachment.setTask(task);
            attachment.setFileName(originalFileName);
            attachment.setStoredFileName(storedFileName);
            attachment.setContentType(file.getContentType());
            attachment.setSize(file.getSize());
            return attachmentRepository.saveAndFlush(attachment);
        } catch (RuntimeException exception) {
            Files.deleteIfExists(targetFile);
            throw exception;
        }
    }

    public TaskAttachment getAttachment(Long attachmentId) {
        return attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new AttachmentNotFoundException(attachmentId));
    }

    public Resource loadAsResource(Long attachmentId) throws IOException {
        TaskAttachment attachment = getAttachment(attachmentId);
        Path filePath = uploadPath.resolve(attachment.getStoredFileName()).normalize();
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            throw new AttachmentNotFoundException(attachmentId);
        }
        return resource;
    }

    @Transactional(rollbackFor = IOException.class)
    public void deleteAttachment(Long attachmentId) throws IOException {
        TaskAttachment attachment = getAttachment(attachmentId);
        attachmentRepository.delete(attachment);
        attachmentRepository.flush();
        Files.deleteIfExists(uploadPath.resolve(attachment.getStoredFileName()));
    }

    public List<TaskAttachment> getAttachmentsByTaskId(Long taskId) {
        taskService.getTask(taskId);
        return attachmentRepository.findAllByTask_IdOrderByUploadedAtAsc(taskId);
    }
}
