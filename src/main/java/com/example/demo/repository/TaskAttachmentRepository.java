package com.example.demo.repository;

import com.example.demo.model.Task;
import com.example.demo.model.TaskAttachment;

import java.util.ArrayList;
import java.util.List;

public class TaskAttachmentRepository {
    List<TaskAttachment> attachments;

    public TaskAttachmentRepository() {
        attachments = new ArrayList<>();
    }

    public List<TaskAttachment> getAttachments() {
        return attachments;
    }

    public void add(TaskAttachment taskAttachment) {
        attachments.add(taskAttachment);
    }

    public void delete(int taskAttachmentId) {
        for (TaskAttachment currentAttachment : attachments) {
            if (currentAttachment.id() == taskAttachmentId) {
                attachments.remove(currentAttachment);
                break;
            }
        }
    }

    public TaskAttachment get(int taskAttachmentId) {
        for (TaskAttachment currentAttachment : attachments) {
            if (currentAttachment.id() == taskAttachmentId) {
                return currentAttachment;
            }
        }
        return null;
    }

    public List<TaskAttachment> getAll() {
        return attachments;
    }
}
