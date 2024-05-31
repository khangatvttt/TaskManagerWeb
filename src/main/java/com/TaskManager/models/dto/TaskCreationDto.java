package com.TaskManager.models.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record TaskCreationDto(
        @NotBlank (message = "Task name can't be leave empty")
        String taskName,
        String description,
        @Max(value = 10, message = "The priority can only be between 1-10")
        @Min(value = 1, message = "The priority can only be between 1-10")
        int priority,
        LocalDateTime dueAt,
        @NotNull
        int taskCreator,
        @NotNull
        int assignedUser) {
}
