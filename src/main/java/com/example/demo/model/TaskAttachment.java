package com.example.demo.model;

import java.time.LocalDateTime;

public class TaskAttachment {
    Long id;
    Long taskId;
    String fileName;
    String storedFileName;
    String contentType;
    long size;
    LocalDateTime uploadedAt;
}
