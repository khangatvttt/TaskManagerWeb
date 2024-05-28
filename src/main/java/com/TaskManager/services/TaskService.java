package com.TaskManager.services;

import com.TaskManager.models.dto.TaskCreationDto;
import com.TaskManager.models.dto.TaskDto;
import com.TaskManager.models.dto.TaskMapper;
import com.TaskManager.models.entities.Task;
import com.TaskManager.models.entities.TaskAssignment;
import com.TaskManager.models.entities.UserAccount;
import com.TaskManager.repositories.TaskAssignmentRepository;
import com.TaskManager.repositories.TaskRepository;
import com.TaskManager.repositories.UserRepository;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskAssignmentRepository taskAssignmentRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository,
                       TaskAssignmentRepository taskAssignmentRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.taskAssignmentRepository = taskAssignmentRepository;
    }

    public TaskAssignment createTask(TaskCreationDto taskCreationDto){
        TaskAssignment createTask = TaskMapper.toTaskAssignment(taskCreationDto);
        Optional<UserAccount> createUser = userRepository.findById(createTask.getUserId().getId());
        Optional<UserAccount> assignedUser = userRepository.findById(createTask.getAssignBy().getId());
        if (assignedUser.isEmpty() || createUser.isEmpty()){
            return null;
        }
        //Set time and default status for task before save
        Task task = createTask.getTaskId();
        task.setCreateAt(LocalDateTime.now());
        task.setStatus(Task.Status.INPROGRESS);
        createTask.setTaskId(taskRepository.save(task));
        //Associate Task to User that will participant
        createTask.setUserId(assignedUser.get());
        createTask.setTaskId(task);
        createTask.setStatus(Task.Status.INPROGRESS);
        //Set user that created this task
        createTask.setAssignBy(createUser.get());
        return taskAssignmentRepository.save(createTask);
    }

    public Task updateTask(Integer taskId, Task updateTask) {
        Task task = taskRepository.findById(taskId).orElse(null);
        if (task==null) {
            return null;
        }
        //Ignore some fields that user doesn't allow to update
        updateTask.setId(null);
        updateTask.setCreateAt(null);
        //Update the task
        task.merge(updateTask);
        return taskRepository.save(task);
    }

    public boolean deleteTask(Integer taskId){
        Task task = taskRepository.findById(taskId).orElse(null);
        if (task==null){
            return false;
        }
        taskRepository.deleteById(taskId);
        return true;
    }
}
