package com.example.demo.config;

import com.example.demo.repository.TaskRepository;
import com.example.demo.service.TaskService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * Процессор для логирования жизненного цикла бинов TaskService и TaskRepository. Реализует
 * интерфейс BeanPostProcessor для перехвата этапов создания и инициализации специфичных бинов
 * Spring контейнера.
 */
@Component
public class TaskLifecycleProcessor implements BeanPostProcessor {

    /**
     * Выполняется перед инициализацией бина. Логирует создание бинов типа TaskService и
     * TaskRepository.
     *
     * @param bean     экземпляр создаваемого бина
     * @param beanName имя бина в Spring контейнере
     * @return исходный бин без изменений
     * @throws BeansException если произошла ошибка при обработке бина
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
            throws BeansException {
        if (bean instanceof TaskService) {
            System.out.println("Before initialization: TaskService [" + beanName + "]");
        }

        if (bean instanceof TaskRepository) {
            System.out.println("Before initialization: TaskRepository [" + beanName + "]");
        }

        return bean;
    }

    /**
     * Выполняется после инициализации бина. Логирует завершение инициализации бинов типа
     * TaskService и TaskRepository.
     *
     * @param bean     экземпляр инициализированного бина
     * @param beanName имя бина в Spring контейнере
     * @return исходный бин без изменений
     * @throws BeansException если произошла ошибка при обработке бина
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {

        if (bean instanceof TaskService) {
            System.out.println("After initialization: TaskService [" + beanName + "]");
        }

        if (bean instanceof TaskRepository) {
            System.out.println("After initialization: TaskRepository [" + beanName + "]");
        }

        return bean;
    }
}
