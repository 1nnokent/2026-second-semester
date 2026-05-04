package com.example.demo.exception;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(int taskId) {
        super("Task with id " + taskId + " was not found");
    }
}
