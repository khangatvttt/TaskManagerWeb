package com.TaskManager.models.dto;

import com.TaskManager.models.entities.Task;
import com.TaskManager.models.entities.TaskAssignment;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TaskAssignmentDto {
    private UserDto taskExecutor;
    private TaskDto task;
    private UserDto taskCreator;
    private Task.Status status;
}
