package com.TaskManager.controllers;

import com.TaskManager.models.dto.TaskCreationDto;
import com.TaskManager.models.dto.TaskDto;
import com.TaskManager.models.entities.Task;
import com.TaskManager.models.entities.TaskAssignment;
import com.TaskManager.models.entities.UserAccount;
import com.TaskManager.models.entities.UserTaskPK;
import com.TaskManager.repositories.TaskAssignmentRepository;
import com.TaskManager.services.TaskService;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/task")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    //Create new Task
    @PostMapping("")
    public ResponseEntity<TaskAssignment> createTask(@RequestBody TaskCreationDto taskCreationDto){
        TaskAssignment taskCreated = taskService.createTask(taskCreationDto);
        return new ResponseEntity<>(taskCreated, taskCreated!=null?HttpStatus.CREATED:HttpStatus.BAD_REQUEST);
    }

    //Edit a Task
    @PatchMapping("/{taskId}")
    public ResponseEntity<Task> editTask(@PathVariable("taskId") Integer taskId, @RequestBody Task updateTask) {
        Task updatedTask = taskService.updateTask(taskId,updateTask);
        return new ResponseEntity<>(updatedTask, updatedTask!=null?HttpStatus.OK:HttpStatus.BAD_REQUEST);
    }

    //Delete a Task
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable("taskId") Integer taskId) throws JsonMappingException {
        return new ResponseEntity<>(taskService.deleteTask(taskId)?HttpStatus.OK:HttpStatus.BAD_REQUEST);
    }
}


