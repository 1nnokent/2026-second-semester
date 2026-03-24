package com.example.demo.service;

import com.example.demo.model.TaskAttachment;
import com.example.demo.repository.TaskAttachmentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class AttachmentService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    private final TaskAttachmentRepository attachmentRepository;
    private Long idSequence = 1L;

    public AttachmentService(TaskAttachmentRepository attachmentRepository) {
        this.attachmentRepository = attachmentRepository;
    }

    public TaskAttachment storeAttachment(Long taskId, MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String storedFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Files.copy(file.getInputStream(), uploadPath.resolve(storedFileName));

        TaskAttachment attachment = new TaskAttachment(
                idSequence++,
                taskId,
                file.getOriginalFilename(),
                storedFileName,
                file.getContentType(),
                file.getSize(),
                LocalDateTime.now()
        );

        attachmentRepository.add(attachment);
        return attachment;
    }

    public TaskAttachment getAttachment(Long attachmentId) {
        TaskAttachment attachment = attachmentRepository.get(attachmentId.intValue());
        if (attachment == null) {
            throw new RuntimeException("Файл не найден: " + attachmentId);
        }
        return attachment;
    }

    public Resource loadAsResource(Long attachmentId) throws IOException {
        TaskAttachment attachment = getAttachment(attachmentId);

        Path filePath = Paths.get(uploadDir).resolve(attachment.storedFileName());
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            throw new IOException("Файл не найден на диске: " + attachment.storedFileName());
        }

        return resource;
    }

    public void deleteAttachment(Long attachmentId) throws IOException {
        TaskAttachment attachment = getAttachment(attachmentId);

        Files.deleteIfExists(Paths.get(uploadDir).resolve(attachment.storedFileName()));
        attachmentRepository.delete(attachmentId.intValue());
    }
}