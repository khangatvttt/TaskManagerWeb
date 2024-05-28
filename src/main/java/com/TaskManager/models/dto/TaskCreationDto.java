package com.TaskManager.models.dto;

import com.TaskManager.models.entities.Task;

import java.time.LocalDateTime;

public record TaskCreationDto(String taskName, String description,
                              int priority, LocalDateTime dueAt,
                              int createUser, int assignedUser) {
}
