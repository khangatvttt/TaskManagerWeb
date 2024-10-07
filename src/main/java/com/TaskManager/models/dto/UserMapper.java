package com.TaskManager.models.dto;

import com.TaskManager.models.entities.TaskAssignment;
import com.TaskManager.models.entities.UserAccount;
import com.TaskManager.models.entities.UserTaskPK;

public class UserMapper {

    public UserMapper(){

    }

    public static UserDto toUserDto(UserAccount userAccount){
        return new UserDto(
                userAccount.getId(),
                userAccount.getName(),userAccount.getEmail(),
                userAccount.getProfilePicture(),
                userAccount.getGender(),
                userAccount.getBirthdate(),
                userAccount.getCreateAt(),
                userAccount.getMainJob(),
                userAccount.getAddress()
        );
    }

    public static UserTaskDTO toUserTaskDto(TaskAssignment taskAssignment){
        return new UserTaskDTO(
                taskAssignment.getTaskExecutor().getId(),
                taskAssignment.getTaskExecutor().getName(),
                taskAssignment.getTaskExecutor().getProfilePicture(),
                taskAssignment.getSubTaskName(),
                taskAssignment.getStatus(),
                taskAssignment.getProgression(),
                taskAssignment.getTask().getTaskName()
        );
    }

}
