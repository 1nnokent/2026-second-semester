package com.example.demo.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(validatedBy = DueDateNotBeforeCreationValidator.class)
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DueDateNotBeforeCreation {
    String message() default "dueDate не может быть раньше даты создания задачи";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}