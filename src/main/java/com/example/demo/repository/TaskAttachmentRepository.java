package com.example.demo.repository;

import com.example.demo.model.TaskAttachment;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class TaskAttachmentRepository {

    private final List<TaskAttachment> attachments = new ArrayList<>();

    public void add(TaskAttachment attachment) {
        attachments.add(attachment);
    }

    public TaskAttachment get(Long attachmentId) {
        for (TaskAttachment attachment : attachments) {
            if (attachment.id().equals(attachmentId)) {
                return attachment;
            }
        }
        return null;
    }

    public void delete(Long attachmentId) {
        Iterator<TaskAttachment> iterator = attachments.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().id().equals(attachmentId)) {
                iterator.remove();
                return;
            }
        }
    }

    public List<TaskAttachment> getAll() {
        return attachments;
    }

    public List<TaskAttachment> findAllByTaskId(Long taskId) {
        return attachments.stream()
                .filter(attachment -> attachment.taskId().equals(taskId))
                .toList();
    }
}
