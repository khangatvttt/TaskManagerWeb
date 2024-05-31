package com.TaskManager.models.dto;

import com.TaskManager.models.entities.Task;
import com.TaskManager.models.entities.TaskAssignment;
import com.TaskManager.models.entities.UserAccount;

public class TaskMapper {

    public TaskMapper() {

    }

    public static Task toTask(TaskDto taskDto){
        Task task = new Task();
        task.setTaskName(taskDto.taskName());
        task.setPriority(taskDto.priority());
        task.setDescription(taskDto.description());
        task.setStatus(taskDto.status());
        return task;
    }

    public static TaskDto toTaskDto(Task task) {
        return new TaskDto(task.getTaskName(), task.getDescription(), task.getPriority(),
                task.getDueAt(),task.getCreateAt(),task.getStatus());
    }

    public static TaskAssignment toTaskAssignment(TaskCreationDto taskCreationDto){
        Task task = new Task();
        task.setTaskName(taskCreationDto.taskName());
        task.setPriority(taskCreationDto.priority());
        task.setDescription(taskCreationDto.description());
        task.setDueAt(taskCreationDto.dueAt());

        UserAccount assignedUser = new UserAccount();
        assignedUser.setId(taskCreationDto.assignedUser());

        UserAccount taskCreator = new UserAccount();
        taskCreator.setId(taskCreationDto.taskCreator());

        TaskAssignment taskAssignment = new TaskAssignment();
        taskAssignment.setTask(task);


        taskAssignment.setTaskExecutor(assignedUser);
        taskAssignment.setTaskCreator(taskCreator);

        return taskAssignment;
    }

    public static TaskAssignmentDto toTaskAssignmentDto(TaskAssignment taskAssignment){
        TaskAssignmentDto taskAssignmentDto = new TaskAssignmentDto();
        taskAssignmentDto.setTaskCreator(UserMapper.toUserDto(taskAssignment.getTaskCreator()));
        taskAssignmentDto.setTaskExecutor(UserMapper.toUserDto(taskAssignment.getTaskExecutor()));
        taskAssignmentDto.setStatus(taskAssignment.getStatus());
        taskAssignmentDto.setTask(TaskMapper.toTaskDto(taskAssignment.getTask()));
        return taskAssignmentDto;
    }
}
