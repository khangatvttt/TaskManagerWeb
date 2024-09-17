package com.TaskManager.controllers;

import com.TaskManager.models.dto.TaskAssignmentDto;
import com.TaskManager.models.dto.TaskDto;
import com.TaskManager.models.dto.TaskSummaryDto;
import com.TaskManager.models.dto.UserDto;
import com.TaskManager.models.entities.Notification;
import com.TaskManager.models.entities.Task;
import com.TaskManager.models.entities.TaskAssignment;
import com.TaskManager.models.entities.UserAccount;
import com.TaskManager.services.TaskService;
import com.TaskManager.services.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("api/v1/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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
    public ResponseEntity<String> updateUser(@PathVariable("userId") Integer userId,
                                             @RequestPart(value = "image", required = false) MultipartFile image,
                                             @ModelAttribute UserAccount updateUser
                                             ){
        String result = userService.updateUser(updateUser,userId, image);
        if (result.equals("OK")) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{userId}/tasks")
    public ResponseEntity<List<TaskAssignmentDto>> getTasksByUser(@PathVariable("userId") Integer userId){
        return new ResponseEntity<>(userService.getTasksByUser(userId), HttpStatus.OK);
    }

    @GetMapping("/{userId}/tasksummary")
    public ResponseEntity<TaskSummaryDto> getTaskSummary(@PathVariable("userId") Integer userId){
        return new ResponseEntity<>(userService.getTaskSummary(userId), HttpStatus.OK);
    }

    @GetMapping("/{userId}/notification")
    public ResponseEntity<List<Notification>> getLatestNotifications(
            @PathVariable("userId") Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Page<Notification> notificationsPage = userService.getLatestNotifications(userId, page, size);
        List<Notification> notifications = notificationsPage.getContent();
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

}
