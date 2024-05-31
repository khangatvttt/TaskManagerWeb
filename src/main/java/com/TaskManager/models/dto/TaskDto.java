package com.TaskManager.models.dto;

import com.TaskManager.models.entities.Task;

import java.time.LocalDateTime;

public record TaskDto(
        String taskName,
        String description,
        int priority,
        LocalDateTime dueAt,
        LocalDateTime createAt,
        Task.Status status) {
}
