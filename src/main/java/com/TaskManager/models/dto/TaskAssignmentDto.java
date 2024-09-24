package com.TaskManager.models.dto;

import com.TaskManager.models.entities.Task;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


public record TaskAssignmentDto (
    Integer taskId,
    String taskName,
    String subTask,
    LocalDateTime dueAt,
    Task.Status status,
    Boolean accept){

}
