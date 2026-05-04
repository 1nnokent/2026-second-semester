package com.example.demo.config;

import com.example.demo.repository.StubTaskRepository;
import com.example.demo.repository.TaskRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Класс конфигурации Spring для регистрации дополнительных бинов приложения. Содержит явное
 * определение бинов через методы с аннотацией @Bean.
 */
@Configuration
public class AppConfig {

    /**
     * Создает и регистрирует bean StubTaskRepository в контексте Spring. Этот репозиторий содержит
     * предопределенные тестовые данные и используется для демонстрации работы с несколькими
     * реализациями одного интерфейса.
     *
     * @return экземпляр StubTaskRepository с тестовыми данными
     */
    @Bean
    public TaskRepository stubTaskRepository() {
        return new StubTaskRepository();
    }
}