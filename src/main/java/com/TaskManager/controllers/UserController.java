package com.TaskManager.controllers;

import com.TaskManager.models.dto.TaskAssignmentDto;
import com.TaskManager.models.dto.UserDto;
import com.TaskManager.models.entities.Task;
import com.TaskManager.models.entities.TaskAssignment;
import com.TaskManager.models.entities.UserAccount;
import com.TaskManager.services.TaskService;
import com.TaskManager.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("")
    public ResponseEntity<Void> createUser(@Valid @RequestBody UserAccount userAccount){
        userService.createUser(userAccount);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") Integer userId){
        userService.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserInfo(@PathVariable("userId") Integer userId){
        return new ResponseEntity<>(userService.getUserInfo(userId),HttpStatus.OK);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Void> updateUser(@PathVariable("userId") Integer userId,@RequestBody UserAccount updateUser){
        userService.updateUser(updateUser,userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{userId}/tasks")
    public ResponseEntity<List<TaskAssignmentDto>> getTasksByUser(@PathVariable("userId") Integer userId){
        return new ResponseEntity<>(userService.getTasksByUser(userId), HttpStatus.OK);
    }

}
