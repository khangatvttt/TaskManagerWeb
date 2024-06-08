package com.TaskManager.controllers;

import com.TaskManager.models.dto.TaskDto;
import com.TaskManager.models.dto.UserDto;
import com.TaskManager.models.entities.Task;
import com.TaskManager.models.entities.TaskAssignment;
import com.TaskManager.services.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/task")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("{taskId}")
    public ResponseEntity<TaskDto> getTask(@PathVariable("taskId") Integer taskId){
        TaskDto taskDto = taskService.getTask(taskId);
        return new ResponseEntity<>(taskDto, HttpStatus.OK);
    }

    //Create new Task
    @PostMapping("")
    public ResponseEntity<Void> createTask(@Valid @RequestBody TaskDto taskDto){
        taskService.createTask(taskDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    //Edit a Task
    @PatchMapping("/{taskId}")
    public ResponseEntity<Void> editTask(@PathVariable("taskId") Integer taskId,@RequestBody Task updateTask) {
        taskService.updateTask(taskId,updateTask);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //Delete a Task
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable("taskId") Integer taskId){
        taskService.deleteTask(taskId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //Assign Task to User
    @PostMapping("/{taskId}/user/{userId}")
    public ResponseEntity<String> assignTaskToUser(@PathVariable("taskId") Integer taskId,
                                                 @PathVariable("userId") Integer userId){
        boolean flag = taskService.assignTaskToUser(taskId,userId);
        if (flag) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>("User {"+userId+"} already has been assigned to task {"+taskId+"}",HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/{taskId}/user/{userId}")
    public ResponseEntity<String> updateTaskAssignment(@PathVariable("taskId") Integer taskId,
                                                       @PathVariable("userId") Integer userId,
                                                        @RequestBody TaskAssignment taskAssignment) {
        boolean flag = taskService.updateTaskAssignment(taskId,userId,taskAssignment);
        if (flag){
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>("User {"+userId+"} hasn't been assigned to task {"+taskId+"} yet",HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{taskId}/user/{userId}")
    public ResponseEntity<String> cancelTaskAssignment(@PathVariable("taskId") Integer taskId,
                                                       @PathVariable("userId") Integer userId){
        boolean flag = taskService.cancelTaskAssignment(taskId,userId);
        if (flag){
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>("User {"+userId+"} hasn't been assigned to task {"+taskId+"} yet",HttpStatus.BAD_REQUEST);
        }
    }
    //Get all users that participant in the task
    @GetMapping("/{taskId}/users")
    public ResponseEntity<List<UserDto>> getUsersInTask(@PathVariable("taskId") Integer taskId){
        List<UserDto> userDtoList = taskService.getExecutorInTask(taskId);
        if (userDtoList!=null) {
            return new ResponseEntity<>(userDtoList, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }


}


