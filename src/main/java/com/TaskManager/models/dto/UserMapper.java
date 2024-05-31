package com.TaskManager.models.dto;

import com.TaskManager.models.entities.UserAccount;

public class UserMapper {

    public UserMapper(){

    }

    public static UserDto toUserDto(UserAccount userAccount){
        return new UserDto(userAccount.getName(),userAccount.getEmail(),userAccount.getProfilePicture());
    }
}
