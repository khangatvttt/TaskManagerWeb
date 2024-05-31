package com.TaskManager.services;

import com.TaskManager.models.dto.TaskCreationDto;
import com.TaskManager.models.dto.TaskMapper;
import com.TaskManager.models.entities.Task;
import com.TaskManager.models.entities.TaskAssignment;
import com.TaskManager.models.entities.UserAccount;
import com.TaskManager.repositories.TaskAssignmentRepository;
import com.TaskManager.repositories.TaskRepository;
import com.TaskManager.repositories.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.Validation;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
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

    public void createTask(TaskCreationDto taskCreationDto){
        TaskAssignment createTask = TaskMapper.toTaskAssignment(taskCreationDto);
        Optional<UserAccount> executor = userRepository.findById(taskCreationDto.taskCreator());
        Optional<UserAccount> assignedUser = userRepository.findById(taskCreationDto.assignedUser());
        if (assignedUser.isEmpty() || executor.isEmpty()){
            if (assignedUser.isEmpty()) {
                throw new NoSuchElementException("User with id {"+taskCreationDto.assignedUser()+"} doesn't exist");
            }
            else {
                throw new NoSuchElementException("User with id {"+taskCreationDto.taskCreator()+"} doesn't exist");
            }
        }
        //Set time and default status for task before save
        Task task = createTask.getTask();
        task.setCreateAt(LocalDateTime.now());
        task.setStatus(Task.Status.INPROGRESS);
        createTask.setTask(taskRepository.save(task));
        //Associate Task to User that will participant
        createTask.setTaskExecutor(assignedUser.get());
        createTask.setTask(task);
        createTask.setStatus(Task.Status.INPROGRESS);
        //Set user that created this task
        createTask.setTaskCreator(executor.get());
        taskAssignmentRepository.save(createTask);
    }

    public void updateTask(Integer taskId, Task updateTask) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            throw new NoSuchElementException("Task with id {"+taskId+"} doesn't exist");
        }
        Task task = taskOpt.get();
        //Ignore some fields that user doesn't allow to update
        updateTask.setId(null);
        updateTask.setCreateAt(null);
        //Update the task
        task.merge(updateTask);
        Validation.buildDefaultValidatorFactory().getValidator().validate(task);
        taskRepository.save(task);
    }


    public void deleteTask(Integer taskId){
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            throw new NoSuchElementException("Task with id {"+taskId+"} doesn't exist");
        }
        taskRepository.delete(taskOpt.get());
    }

}
