package com.example.demo.validation;

import com.example.demo.dto.TaskUpdateDto;
import com.example.demo.model.Task;
import com.example.demo.repository.TaskRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraintvalidation.SupportedValidationTarget;
import jakarta.validation.constraintvalidation.ValidationTarget;
import org.springframework.stereotype.Component;

@Component
@SupportedValidationTarget(ValidationTarget.PARAMETERS)
public class DueDateNotBeforeCreationValidator
        implements ConstraintValidator<DueDateNotBeforeCreation, Object[]> {

    private final TaskRepository taskRepository;

    public DueDateNotBeforeCreationValidator(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public boolean isValid(Object[] parameters, ConstraintValidatorContext context) {
        if (parameters == null || parameters.length < 2) {
            return true;
        }

        Object rawTaskId = parameters[0];
        Object rawDto = parameters[1];
        if (!(rawTaskId instanceof Integer taskId) || !(rawDto instanceof TaskUpdateDto dto)) {
            return true;
        }

        if (dto.dueDate() == null) {
            return true;
        }

        Task task = taskRepository.get(taskId);
        if (task == null || task.getCreatedAt() == null) {
            return true;
        }

        return !dto.dueDate().isBefore(task.getCreatedAt().toLocalDate());
    }
}
