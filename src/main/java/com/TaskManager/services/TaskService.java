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
import lombok.SneakyThrows;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.naming.NoPermissionException;
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

    @SneakyThrows
    public void checkPermission(UserAccount owner){
        UserAccount currentUser = (UserAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!currentUser.getId().equals(owner.getId())) {
            throw new NoPermissionException();
        }

    }

    @SneakyThrows
    public TaskDto getTask(Integer taskId){
        Task task = checkTaskId(taskId);
        checkPermission(task.getCreator());
        return TaskMapper.toTaskDto(checkTaskId(taskId));
    }

    public void createTask(TaskDto taskDto) {
        UserAccount currentUser = (UserAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Task task = TaskMapper.toTask(taskDto);
        task.setCreateAt(LocalDateTime.now());
        task.setCreator(currentUser);
        task.setStatus(Task.Status.INPROGRESS);
        taskRepository.save(task);
    }

    public void updateTask(Integer taskId, Task updateTask) {
        Task task = checkTaskId(taskId);
        checkPermission(task.getCreator());
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
        checkPermission(task.getCreator());
        taskRepository.delete(task);
    }

    public List<UserDto> getExecutorInTask(Integer taskId) {
        Task task = checkTaskId(taskId);
        checkPermission(task.getCreator());
        List<TaskAssignment> taskAssignmentList = task.getTaskAssignments();
        return taskAssignmentList.stream()
                .map(taskAssignment -> UserMapper.toUserDto(taskAssignment.getTaskExecutor()))
                .collect(Collectors.toList());
    }

    public boolean assignTaskToUser(Integer taskId, Integer userId){
        Task task = checkTaskId(taskId);
        UserAccount user = checkUserId(userId);
        checkPermission(task.getCreator());
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

    //Update status, refuse or accept task assignment
    public boolean updateTaskAssignment(Integer taskId, Integer userId, TaskAssignment taskAssignment){
        checkTaskId(taskId);
        UserAccount user = checkUserId(userId);
        Optional<TaskAssignment> checkExist = taskAssignmentRepository.findById(new UserTaskPK(userId,taskId));
        if (checkExist.isEmpty()){
            return false;
        }
        checkPermission(user);
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

    @SneakyThrows
    public boolean cancelTaskAssignment(Integer taskId, Integer userId){
        Task task = checkTaskId(taskId);
        UserAccount user = checkUserId(userId);
        Optional<TaskAssignment> checkExist = taskAssignmentRepository.findById(new UserTaskPK(userId,taskId));
        if (checkExist.isEmpty()){
            return false;
        }
        UserAccount currentAuthUser = (UserAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //Only owner of the task or executor of task can cancel the assignment
        if (!currentAuthUser.getId().equals(user.getId()) || !currentAuthUser.getId().equals(task.getCreator().getId())){
            throw new NoPermissionException();
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
