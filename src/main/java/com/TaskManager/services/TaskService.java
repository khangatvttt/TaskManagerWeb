package com.TaskManager.services;

import com.TaskManager.models.dto.TaskDto;
import com.TaskManager.models.dto.TaskMapper;
import com.TaskManager.models.dto.UserDto;
import com.TaskManager.models.dto.UserMapper;
import com.TaskManager.models.entities.Task;
import com.TaskManager.models.entities.TaskAssignment;
import com.TaskManager.models.entities.UserAccount;
import com.TaskManager.models.entities.UserTaskPK;
import com.TaskManager.repositories.TaskAssignmentRepository;
import com.TaskManager.repositories.TaskRepository;
import com.TaskManager.repositories.UserRepository;
import jakarta.validation.Validation;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public TaskDto getTask(Integer taskId){
        return TaskMapper.toTaskDto(checkTaskId(taskId));
    }

    public void createTask(TaskDto taskDto) {
        Task task = TaskMapper.toTask(taskDto);
        task.setCreateAt(LocalDateTime.now());
        task.setCreator(checkUserId(taskDto.creatorId()));
        task.setStatus(Task.Status.INPROGRESS);
        taskRepository.save(task);
    }

    public void updateTask(Integer taskId, Task updateTask) {
        Task task = checkTaskId(taskId);
        //Ignore some fields that user doesn't allow to update
        updateTask.setId(null);
        updateTask.setCreateAt(null);
        updateTask.setCreator(null);
        //Update the task
        task.merge(updateTask);
        Validation.buildDefaultValidatorFactory().getValidator().validate(task);
        taskRepository.save(task);
    }


    public void deleteTask(Integer taskId) {
        Task task = checkTaskId(taskId);
        taskRepository.delete(task);
    }

    public List<UserDto> getUsersInTask(Integer taskId) {
        Task task = checkTaskId(taskId);
        List<TaskAssignment> taskAssignmentList = task.getTaskAssignments();
        return taskAssignmentList.stream()
                .map(taskAssignment -> UserMapper.toUserDto(taskAssignment.getTaskExecutor()))
                .collect(Collectors.toList());
    }

    public boolean assignTaskToUser(Integer taskId, Integer userId){
        Task task = checkTaskId(taskId);
        UserAccount user = checkUserId(userId);
        Optional<TaskAssignment> checkExist = taskAssignmentRepository.findById(new UserTaskPK(userId,taskId));
        if (checkExist.isPresent()){
            return false;
        }
        TaskAssignment taskAssignment = new TaskAssignment();
        taskAssignment.setTaskExecutor(user);
        taskAssignment.setTask(task);
        taskAssignment.setAssignedAt(LocalDateTime.now());
        taskAssignment.setStatus(Task.Status.INPROGRESS);
        taskAssignmentRepository.save(taskAssignment);
        return true;
    }

    public boolean updateTaskAssignment(Integer taskId, Integer userId, TaskAssignment taskAssignment){
        checkTaskId(taskId);
        checkUserId(userId);
        Optional<TaskAssignment> checkExist = taskAssignmentRepository.findById(new UserTaskPK(userId,taskId));
        if (checkExist.isEmpty()){
            return false;
        }
        TaskAssignment updatedTaskAssignment = checkExist.get();
        if (taskAssignment.getStatus()!=null) {
            updatedTaskAssignment.setStatus(taskAssignment.getStatus());
        }
        if (taskAssignment.getIsAccepted()!=null) {
            updatedTaskAssignment.setIsAccepted(taskAssignment.getIsAccepted());
        }
        taskAssignmentRepository.save(updatedTaskAssignment);
        return true;
    }

    public boolean cancelTaskAssignment(Integer taskId, Integer userId){
        Task task = checkTaskId(taskId);
        UserAccount user = checkUserId(userId);
        Optional<TaskAssignment> checkExist = taskAssignmentRepository.findById(new UserTaskPK(userId,taskId));
        if (checkExist.isEmpty()){
            return false;
        }
        TaskAssignment taskAssignment = checkExist.get();
        List<TaskAssignment> taskAssignmentList = task.getTaskAssignments();
        taskAssignmentList.remove(taskAssignment);
        task.setTaskAssignments(taskAssignmentList);
        taskRepository.save(task);

        taskAssignmentList = user.getTaskAssignments();
        taskAssignmentList.remove(taskAssignment);
        user.setTaskAssignments(taskAssignmentList);
        userRepository.save(user);
        taskAssignmentRepository.deleteById(new UserTaskPK(userId,taskId));

        return true;
    }

    public Task checkTaskId(Integer taskId) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            throw new NoSuchElementException("Task with id {" + taskId + "} doesn't exist");
        }
        return taskOpt.get();
    }

    public UserAccount checkUserId(Integer userId) {
        Optional<UserAccount> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new NoSuchElementException("User with id {" + userId + "} doesn't exist");
        }
        return userOpt.get();
    }
}
