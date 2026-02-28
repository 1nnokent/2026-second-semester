package com.example.demo.service;

import com.example.demo.model.Task;
import com.example.demo.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Сервис для работы со статистикой и сравнением различных репозиториев задач. Демонстрирует
 * использование аннотации @Qualifier для внедрения нескольких реализаций одного интерфейса.
 * Позволяет определить, в каком из репозиториев находится конкретная задача.
 */
@Service
public class TaskStatisticsService {

    /**
     * Основной репозиторий задач (помеченный как @Primary)
     */
    private final TaskRepository primary;

    /**
     * Вторичный репозиторий задач (StubTaskRepository с тестовыми данными)
     */
    private final TaskRepository secondary;

    /**
     * Конструктор сервиса с внедрением двух репозиториев. Первый репозиторий внедряется
     * автоматически как @Primary, второй явно указывается через @Qualifier.
     *
     * @param primary   основной репозиторий (InMemoryTaskRepository)
     * @param secondary вторичный репозиторий (StubTaskRepository), указывается явно через
     *                  @Qualifier
     */
    public TaskStatisticsService(TaskRepository primary,
            @Qualifier("stubTaskRepository") TaskRepository secondary) {
        this.primary = primary;
        this.secondary = secondary;
    }

    /**
     * Определяет, в каком репозитории находится указанная задача. Сначала проверяет основной
     * репозиторий, затем вторичный.
     *
     * @param task задача для поиска
     * @return репозиторий, содержащий задачу (primary, если задача найдена в нём, иначе secondary)
     */
    public TaskRepository getRepository(Task task) {
        if (primary.find(task) != -1) {
            return primary;
        }
        return secondary;
    }
}
