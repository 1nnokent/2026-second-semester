package com.example.demo.exception;

public class AttachmentNotFoundException extends RuntimeException {

    public AttachmentNotFoundException(Long attachmentId) {
        super("Attachment with id " + attachmentId + " was not found");
    }
}
