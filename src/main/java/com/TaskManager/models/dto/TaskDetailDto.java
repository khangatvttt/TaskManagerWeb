package com.TaskManager.models.dto;

import com.TaskManager.models.entities.Task;

import java.time.LocalDateTime;

public record TaskDetailDto(
        String taskName,
        String description,
        LocalDateTime createAt,
        LocalDateTime dueAt,
        String taskOwner,
        Integer taskOwnerId,
        int teamSize,
        String subTask,
        Task.Status status,
        Task.Status personalStatus,
        LocalDateTime assignedAt,
        int priority,
        int progression){

}
