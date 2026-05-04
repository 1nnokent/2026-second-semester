package com.example.demo.service;

import com.example.demo.exception.AttachmentNotFoundException;
import com.example.demo.model.TaskAttachment;
import com.example.demo.repository.TaskAttachmentRepository;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AttachmentService {

    private final TaskAttachmentRepository attachmentRepository;
    private final TaskService taskService;
    private final AtomicLong idSequence = new AtomicLong(1L);
    private final Path uploadPath;

    public AttachmentService(TaskAttachmentRepository attachmentRepository,
            TaskService taskService,
            @Value("${app.upload.dir}") String uploadDir) {
        this.attachmentRepository = attachmentRepository;
        this.taskService = taskService;
        this.uploadPath = Path.of(uploadDir).toAbsolutePath().normalize();
    }

    public TaskAttachment storeAttachment(Long taskId, MultipartFile file) throws IOException {
        taskService.getTask(taskId.intValue());
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

        TaskAttachment attachment = new TaskAttachment(
                idSequence.getAndIncrement(),
                taskId,
                originalFileName,
                storedFileName,
                file.getContentType(),
                file.getSize(),
                LocalDateTime.now()
        );
        attachmentRepository.add(attachment);
        return attachment;
    }

    public TaskAttachment getAttachment(Long attachmentId) {
        TaskAttachment attachment = attachmentRepository.get(attachmentId);
        if (attachment == null) {
            throw new AttachmentNotFoundException(attachmentId);
        }
        return attachment;
    }

    public Resource loadAsResource(Long attachmentId) throws IOException {
        TaskAttachment attachment = getAttachment(attachmentId);
        Path filePath = uploadPath.resolve(attachment.storedFileName()).normalize();
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            throw new AttachmentNotFoundException(attachmentId);
        }
        return resource;
    }

    public void deleteAttachment(Long attachmentId) throws IOException {
        TaskAttachment attachment = getAttachment(attachmentId);
        Files.deleteIfExists(uploadPath.resolve(attachment.storedFileName()));
        attachmentRepository.delete(attachmentId);
    }

    public List<TaskAttachment> getAttachmentsByTaskId(Long taskId) {
        taskService.getTask(taskId.intValue());
        return attachmentRepository.findAllByTaskId(taskId);
    }
}
