package com.example.demo.repository;

import com.example.demo.model.Task;
import java.util.ArrayList;
import java.util.List;

/**
 * Реализация репозитория задач с предопределенными тестовыми данными (заглушка). При создании
 * автоматически заполняется 10 тестовыми задачами. Используется для демонстрации работы с
 * несколькими реализациями одного интерфейса и регистрируется как bean через @Bean метод в
 * конфигурационном классе.
 */
public class StubTaskRepository implements TaskRepository {

    /**
     * Список задач с предопределенными тестовыми данными
     */
    List<Task> tasks;

    /**
     * Конструктор, создающий репозиторий с 10 предопределенными задачами. Задачи имеют
     * идентификаторы от 0 до 9 и автоматически сгенерированные названия и описания.
     */
    public StubTaskRepository() {
        tasks = new ArrayList<>();
        for (int i = 0; i < 10; ++i) {
            tasks.add(new Task(i, "Title" + i, "Description" + i, false));
        }
    }

    /**
     * Фабричный метод для создания экземпляра StubTaskRepository.
     *
     * @return новый экземпляр StubTaskRepository
     */
    public TaskRepository stubTaskRepository() {
        return new StubTaskRepository();
    }

    /**
     * Метод добавления задачи (заглушка). В данной реализации не выполняет никаких действий, так
     * как репозиторий содержит фиксированный набор данных.
     *
     * @param task задача для добавления (игнорируется)
     */
    @Override
    public void add(Task task) {

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
     * @return список всех предопределенных задач
     */
    @Override
    public List<Task> getAll() {
        return this.tasks;
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
