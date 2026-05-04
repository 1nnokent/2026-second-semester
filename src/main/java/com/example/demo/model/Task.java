package com.example.demo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Schema(description = "Internal task entity stored by the application")
public class Task {

    private int id;
    private String title;
    private String description;
    private boolean completed;
    private LocalDateTime createdAt;
    private LocalDate dueDate;
    private Priority priority;
    private Set<String> tags = new LinkedHashSet<>();

    public Task() {
    }

    public Task(int id, String title, String description, boolean completed) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.completed = completed;
    }

    public Task(int id, String title, String description, boolean completed,
            LocalDateTime createdAt, LocalDate dueDate, Priority priority, Set<String> tags) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.completed = completed;
        this.createdAt = createdAt;
        this.dueDate = dueDate;
        this.priority = priority;
        setTags(tags);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public boolean getCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags == null ? new LinkedHashSet<>() : new LinkedHashSet<>(tags);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Task other)) {
            return false;
        }
        return id == other.id
                && completed == other.completed
                && Objects.equals(title, other.title)
                && Objects.equals(description, other.description)
                && Objects.equals(createdAt, other.createdAt)
                && Objects.equals(dueDate, other.dueDate)
                && priority == other.priority
                && Objects.equals(tags, other.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, completed, createdAt, dueDate, priority, tags);
    }

    @Override
    public String toString() {
        return "Task{"
                + "id=" + id
                + ", title='" + title + '\''
                + ", completed=" + completed
                + ", createdAt=" + createdAt
                + ", dueDate=" + dueDate
                + ", priority=" + priority
                + ", tags=" + tags
                + '}';
    }
}
