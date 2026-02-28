package com.example.demo.repository;

import com.example.demo.model.Task;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;


/**
 * Реализация репозитория задач с хранением данных в памяти. Использует ArrayList для хранения
 * задач. Данные теряются при перезапуске приложения. Помечен как @Primary, что делает его основной
 * реализацией TaskRepository при внедрении зависимостей.
 */
@Repository
@Primary
public class InMemoryTaskRepository implements TaskRepository {

    /**
     * Список задач, хранящихся в памяти
     */
    List<Task> tasks;

    /**
     * Конструктор, инициализирующий пустой список задач.
     */
    public InMemoryTaskRepository() {
        tasks = new ArrayList<>();
    }

    /**
     * Добавляет новую задачу в репозиторий.
     *
     * @param task задача для добавления
     */
    @Override
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Удаляет задачу по идентификатору. Если задача с указанным ID не найдена, метод ничего не
     * делает.
     *
     * @param taskId идентификатор задачи для удаления
     */
    @Override
    public void delete(int taskId) {
        for (Task currentTask : tasks) {
            if (currentTask.getId() == taskId) {
                tasks.remove(currentTask);
                break;
            }
        }
    }

    /**
     * Получает задачу по идентификатору.
     *
     * @param taskId идентификатор задачи
     * @return задача с указанным идентификатором или null, если не найдена
     */
    @Override
    public Task get(int taskId) {
        for (Task currentTask : tasks) {
            if (currentTask.getId() == taskId) {
                return currentTask;
            }
        }
        return null;
    }

    /**
     * Получает список всех задач из репозитория.
     *
     * @return список всех задач
     */
    @Override
    public List<Task> getAll() {
        return this.tasks;
    }

    /**
     * Находит индекс задачи в списке.
     *
     * @param task задача для поиска
     * @return индекс задачи в списке или -1, если не найдена
     */
    @Override
    public int find(Task task) {
        return tasks.indexOf(task);
    }

    /**
     * Обновляет задачу с указанным идентификатором. Заменяет существующую задачу на новую по ID.
     * Если задача с указанным ID не найдена, метод ничего не делает.
     *
     * @param taskId идентификатор задачи для обновления
     * @param task   новый объект задачи с обновленными данными
     */
    @Override
    public void update(int taskId, Task task) {
        for (int i = 0; i < tasks.size(); ++i) {
            if (tasks.get(i).getId() == taskId) {
                tasks.set(i, task);
                break;
            }
        }
    }
}
