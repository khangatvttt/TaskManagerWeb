package com.TaskManager.models.dto;

import com.TaskManager.models.entities.Task;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


public record TaskAssignmentDto (
    UserDto taskExecutor,
    TaskDto task,
    LocalDateTime assignedAt,
    Task.Status status){

}
