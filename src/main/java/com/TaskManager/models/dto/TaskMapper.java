package com.TaskManager.models.dto;

import com.TaskManager.models.entities.Task;
import com.TaskManager.models.entities.TaskAssignment;

public class TaskMapper {

    public static Task toTask(TaskDto taskDto){
        Task task = new Task();
        task.setTaskName(taskDto.taskName());
        task.setDescription(taskDto.description());
        task.setStatus(taskDto.status());
        task.setDueAt(taskDto.dueAt());
        return task;
    }

    public static TaskDto toTaskDto(Task task) {
        return new TaskDto(task.getTaskName(), task.getDescription(),
                task.getDueAt(),task.getCreateAt(),null,task.getCreator().getId(),task.getStatus());
    }


    public static TaskAssignmentDto toTaskAssignmentDto(TaskAssignment taskAssignment){
        return new TaskAssignmentDto(
                taskAssignment.getTask().getId(),
                taskAssignment.getTask().getTaskName(),
                taskAssignment.getSubTaskName(),
                taskAssignment.getTask().getDueAt(),
                taskAssignment.getStatus(),
                taskAssignment.getIsAccepted()
        );
    }

    public static TaskDetailDto toTaskDetailDto(TaskAssignment taskAssignment){
        return new TaskDetailDto(
                taskAssignment.getTask().getTaskName(),
                taskAssignment.getTask().getDescription(),
                taskAssignment.getTask().getCreateAt(),
                taskAssignment.getTask().getDueAt(),
                taskAssignment.getTask().getCreator().getName(),
                taskAssignment.getTask().getCreator().getId(),
                taskAssignment.getTask().getTaskAssignments().size(),
                taskAssignment.getSubTaskName(),
                taskAssignment.getTask().getStatus(),
                taskAssignment.getStatus(),
                taskAssignment.getAssignedAt(),
                taskAssignment.getPriority(),
                taskAssignment.getProgression(),
                taskAssignment.getIsAccepted()
        );
    }
}
