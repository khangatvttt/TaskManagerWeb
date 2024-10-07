package com.TaskManager.models.dto;

import com.TaskManager.models.entities.Task;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UserTaskDTO(
        Integer id,
        String name,
        String profilePicture,
        String taskName,
        Task.Status status,
        Integer progression,
        String generalTaskName) {
}
