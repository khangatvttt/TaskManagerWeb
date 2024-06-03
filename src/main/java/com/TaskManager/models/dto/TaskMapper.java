package com.TaskManager.models.dto;

import com.TaskManager.models.entities.Task;
import com.TaskManager.models.entities.TaskAssignment;

public class TaskMapper {

    public static Task toTask(TaskDto taskDto){
        Task task = new Task();
        task.setTaskName(taskDto.taskName());
        task.setPriority(taskDto.priority());
        task.setDescription(taskDto.description());
        task.setStatus(taskDto.status());
        task.setDueAt(taskDto.dueAt());
        return task;
    }

    public static TaskDto toTaskDto(Task task) {
        return new TaskDto(task.getTaskName(), task.getDescription(), task.getPriority(),
                task.getDueAt(),task.getCreateAt(),task.getCreator().getId(),task.getStatus());
    }


    public static TaskAssignmentDto toTaskAssignmentDto(TaskAssignment taskAssignment){
        return new TaskAssignmentDto(
                UserMapper.toUserDto(taskAssignment.getTaskExecutor()),
                TaskMapper.toTaskDto(taskAssignment.getTask()),
                taskAssignment.getAssignedAt(),
                taskAssignment.getStatus()
        );
    }
}
