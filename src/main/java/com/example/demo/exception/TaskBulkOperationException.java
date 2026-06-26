package com.example.demo.exception;

import java.util.List;

public class TaskBulkOperationException extends RuntimeException {

    public TaskBulkOperationException(List<Long> missingIds) {
        super("Bulk task completion failed. Missing task ids: " + missingIds);
    }
}
