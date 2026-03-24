package com.example.demo.validation;

import com.example.demo.dto.TaskUpdateDto;
import com.example.demo.model.Task;
import com.example.demo.repository.TaskRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraintvalidation.SupportedValidationTarget;
import jakarta.validation.constraintvalidation.ValidationTarget;
import org.springframework.beans.factory.annotation.Autowired;

@SupportedValidationTarget(ValidationTarget.PARAMETERS)
public class DueDateNotBeforeCreationValidator
        implements ConstraintValidator<DueDateNotBeforeCreation, Object[]> {

    @Autowired
    private TaskRepository taskRepository;

    @Override
    public boolean isValid(Object[] params, ConstraintValidatorContext context) {
        Integer id = (Integer) params[0];
        TaskUpdateDto dto = (TaskUpdateDto) params[1];

        if (dto.dueDate() == null) {
            return true;
        }

        Task task = taskRepository.get(id);
        boolean valid = !dto.dueDate().isBefore(task.getCreatedAt());
        if (!valid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("dueDate").addConstraintViolation();
        }
        return true;
    }
}