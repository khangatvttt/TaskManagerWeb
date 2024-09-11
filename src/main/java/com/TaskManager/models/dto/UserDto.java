package com.TaskManager.models.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UserDto(
        String name,
        String email,
        String profilePicture,
        Boolean gender,
        LocalDate birthdate,
        LocalDateTime createdAt,
        String mainJob,
        String address){
}
