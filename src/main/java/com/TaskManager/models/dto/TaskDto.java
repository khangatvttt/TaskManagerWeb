package com.TaskManager.models.dto;

import com.TaskManager.models.entities.Task;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record TaskDto(
        @NotBlank(message = "Please provide task name")
        String taskName,
        String description,
        LocalDateTime dueAt,
        LocalDateTime createAt,
        @NotNull(message = "Please provide creator of this task")
        int creatorId,
        Task.Status status) {
}
