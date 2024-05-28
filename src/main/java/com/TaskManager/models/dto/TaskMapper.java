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

    public static TaskAssignment toTaskAssignment(TaskCreationDto taskCreationDto){
        Task task = new Task();
        task.setTaskName(taskCreationDto.taskName());
        task.setPriority(taskCreationDto.priority());
        task.setDescription(taskCreationDto.description());
        task.setDueAt(taskCreationDto.dueAt());

        UserAccount assignedUser = new UserAccount();
        assignedUser.setId(taskCreationDto.assignedUser());

        UserAccount createUser = new UserAccount();
        createUser.setId(taskCreationDto.createUser());

        TaskAssignment taskAssignment = new TaskAssignment();
        taskAssignment.setTaskId(task);


        taskAssignment.setUserId(assignedUser);
        taskAssignment.setAssignBy(createUser);

        return taskAssignment;
    }
}
