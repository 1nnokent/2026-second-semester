package com.example.demo.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

/**
 * Модель данных для представления задачи (task). Содержит основную информацию о задаче:
 * идентификатор, название, описание и статус выполнения.
 */
public class Task {

    /**
     * Уникальный идентификатор задачи
     */
    private int id;

    /**
     * Название задачи
     */
    private String title;

    /**
     * Описание задачи
     */
    private String description;

    /**
     * Статус выполнения задачи (true - выполнена, false - не выполнена)
     */
    private boolean completed;

    LocalDateTime createdAt;

    LocalDateTime dueDate;

    Priority priority;

    Set<String> tags;

    /**
     * Конструктор для создания задачи со всеми параметрами.
     *
     * @param id          уникальный идентификатор задачи
     * @param title       название задачи
     * @param description описание задачи
     * @param completed   статус выполнения задачи
     */
    public Task(int id, String title, String description, boolean completed) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.completed = completed;
    }

    /**
     * Устанавливает идентификатор задачи.
     *
     * @param id новый идентификатор задачи
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Устанавливает название задачи.
     *
     * @param title новое название задачи
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Устанавливает описание задачи.
     *
     * @param description новое описание задачи
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Устанавливает статус выполнения задачи.
     *
     * @param completed новый статус выполнения
     */
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    /**
     * Возвращает идентификатор задачи.
     *
     * @return идентификатор задачи
     */
    public int getId() {
        return this.id;
    }

    /**
     * Возвращает название задачи.
     *
     * @return название задачи
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Возвращает описание задачи.
     *
     * @return описание задачи
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Возвращает статус выполнения задачи.
     *
     * @return true если задача выполнена, false в противном случае
     */
    public boolean getCompleted() {
        return this.completed;
    }

    public LocalDateTime getDueDate() {
        return this.dueDate;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    /**
     * Сравнивает эту задачу с другим объектом на равенство. Две задачи считаются равными, если
     * совпадают все их поля: id, title, description и completed.
     *
     * @param obj объект для сравнения
     * @return true если объекты равны, false в противном случае
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Task other)) {
            return false;
        }

        return this.id == other.id
                && Objects.equals(this.title, other.title)
                && Objects.equals(this.description, other.description)
                && this.completed == other.completed;
    }

    /**
     * Возвращает хеш-код задачи на основе всех полей.
     *
     * @return хеш-код объекта
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, completed);
    }

    /**
     * Возвращает строковое представление задачи в формате JSON-подобной структуры.
     *
     * @return строковое представление задачи
     */
    @Override
    public String toString() {
        return "{id=" + id + ",title=" + title + ",description=" + description + ",completed="
                + completed + "}";
    }
}
