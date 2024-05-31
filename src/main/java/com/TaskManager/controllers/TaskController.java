package com.TaskManager.controllers;

import com.TaskManager.models.dto.TaskCreationDto;
import com.TaskManager.models.entities.Task;
import com.TaskManager.services.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/task")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    //Create new Task
    @PostMapping("")
    public ResponseEntity<Void> createTask(@Valid @RequestBody TaskCreationDto taskCreationDto){
        taskService.createTask(taskCreationDto);
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
}


