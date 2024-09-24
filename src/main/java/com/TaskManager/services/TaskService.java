package com.TaskManager.services;

import com.TaskManager.models.dto.*;
import com.TaskManager.models.entities.*;
import com.TaskManager.repositories.NotificationRepository;
import com.TaskManager.repositories.TaskAssignmentRepository;
import com.TaskManager.repositories.TaskRepository;
import com.TaskManager.repositories.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Validation;
import lombok.SneakyThrows;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.naming.NoPermissionException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskAssignmentRepository taskAssignmentRepository;
    private final NotificationRepository notificationRepository;


    public TaskService(TaskRepository taskRepository, UserRepository userRepository,
                       TaskAssignmentRepository taskAssignmentRepository, NotificationRepository notificationRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.taskAssignmentRepository = taskAssignmentRepository;
        this.notificationRepository = notificationRepository;
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
        //Check name unique
        Optional<Task> taskOpt = taskRepository.findByTaskNameAndCreator(taskDto.taskName(),currentUser);
        if (taskOpt.isPresent()){
            throw new DuplicateKeyException("You've already had this task name!");
        }
        //Create Task
        Task task = TaskMapper.toTask(taskDto);
        task.setCreateAt(LocalDateTime.now());
        task.setCreator(currentUser);
        task.setStatus(Task.Status.INPROGRESS);
        taskRepository.save(task);

        //Assign task to its owner
        TaskAssignment taskAssignment = new TaskAssignment();
        taskAssignment.setStatus(Task.Status.INPROGRESS);
        taskAssignment.setIsAccepted(true);
        taskAssignment.setTaskExecutor(currentUser);
        taskAssignment.setTask(task);
        taskAssignment.setProgression(0);
        taskAssignment.setSubTaskName("Manager of this task");
        taskAssignment.setPriority(taskDto.priority());
        taskAssignment.setAssignedAt(LocalDateTime.now());
        taskAssignmentRepository.save(taskAssignment);
    }

    public void updateTask(Integer taskId, Task updateTask) {
        Task task = checkTaskId(taskId);
        List<TaskAssignment> taskAssignmentList = task.getTaskAssignments();
        checkPermission(task.getCreator());
        //Ignore some fields that user doesn't allow to update
        updateTask.setId(null);
        updateTask.setCreateAt(null);
        updateTask.setCreator(null);
        //Update the task
        task.merge(updateTask);
        Validation.buildDefaultValidatorFactory().getValidator().validate(task);
        taskRepository.save(task);

        task = checkTaskId(taskId);
        //Notify to all members of this task about changes
        for (TaskAssignment assignment : taskAssignmentList){
            if (assignment.getIsAccepted()) {
                Notification notification = new Notification();
                notification.setTime(LocalDateTime.now());
                notification.setReceiver(assignment.getTaskExecutor());
                notification.setNotification("\""+task.getTaskName() + "\" task has been updated by manager.");
                notification.setTargetUrl("/user/"+assignment.getTaskExecutor().getId()+"/task/"+task.getId()+"/manage");
                notification.setRead(false);
                notificationRepository.save(notification);
            }
        }
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

    public TaskDetailDto getTaskAssignment(Integer userId, Integer taskId){
        Task task = checkTaskId(taskId);
        UserAccount user = checkUserId(userId);
        checkPermission(user);
        Optional<TaskAssignment> checkExist = taskAssignmentRepository.findById(new UserTaskPK(userId,taskId));
        if (checkExist.isEmpty()){
            return null;
        }
        TaskAssignment taskAssignment = checkExist.get();
        return TaskMapper.toTaskDetailDto(taskAssignment);
    }

    public Map<String,String> assignTaskToUsers(Integer taskId, List<AddMemberDTO> members){
        Task task = checkTaskId(taskId);
        checkPermission(task.getCreator());
        Map<String, String> response = new HashMap<>();
        for (AddMemberDTO member : members){
            Optional<UserAccount> userAccountOpt = userRepository.findByEmail(member.email());
            UserAccount user;
            if (userAccountOpt.isEmpty()){
                response.put(member.email(),"This user doesn't not exist");
                continue;
            }
            user = userAccountOpt.get();
            if (user.getId().equals(task.getCreator().getId())){
                response.put(member.email(),"You can't invite yourself");
                continue;
            }
            Optional<TaskAssignment> checkExist = taskAssignmentRepository.findById(new UserTaskPK(user.getId(),taskId));
            if (checkExist.isPresent()){
                response.put(member.email(),"This user has already been invited to this task");
                continue;
            }
            TaskAssignment taskAssignment = new TaskAssignment();
            taskAssignment.setIsAccepted(false);
            taskAssignment.setTaskExecutor(user);
            taskAssignment.setTask(task);
            taskAssignment.setSubTaskName(member.subTask());
            taskAssignment.setAssignedAt(LocalDateTime.now());
            taskAssignment.setPriority(5);  //Default is 5
            taskAssignment.setStatus(Task.Status.INPROGRESS);
            taskAssignmentRepository.save(taskAssignment);

            //Send notification
            Notification notification = new Notification();
            String content = "User %s (%s) has invited you to join '%s' task with job is '%s'";
            String contentFormatted = String.format(content, task.getCreator().getName(),
                    task.getCreator().getEmail(), task.getTaskName(), member.subTask());
            notification.setNotification(contentFormatted);
            notification.setTime(LocalDateTime.now());
            notification.setReceiver(user);
            notification.setRead(false);
            notification.setTargetUrl("/user/"+user.getId()+"/task/"+task.getId());
            notificationRepository.save(notification);

            response.put(member.email(),"Your invitation has been sent successfully");
        }


        return response;
    }

    //Update task assignment
    public boolean updateTaskAssignment(Integer taskId, Integer userId, TaskAssignment updatedTaskAssignment){
        checkTaskId(taskId);
        UserAccount user = checkUserId(userId);
        Optional<TaskAssignment> checkExist = taskAssignmentRepository.findById(new UserTaskPK(userId,taskId));
        if (checkExist.isEmpty()){
            return false;
        }
        checkPermission(user);
        TaskAssignment taskAssignment = checkExist.get();
        Boolean checkAccept = taskAssignment.getIsAccepted();
        taskAssignment.merge(updatedTaskAssignment);
        taskAssignment.setIsAccepted(true); // Automatically accept task if user update the task
        taskAssignmentRepository.save(taskAssignment);

        //Notify to owner of task about changes
        if (!user.getId().equals(taskAssignment.getTask().getCreator().getId())) {
            Notification notification = new Notification();
            notification.setRead(false);
            notification.setTime(LocalDateTime.now());
            notification.setReceiver(taskAssignment.getTask().getCreator());
            if (checkAccept) {
                notification.setNotification(user.getName() + " has updated " + (user.getGender() ? "his" : "her")
                        + " personal status in \"" + taskAssignment.getTask().getTaskName() + "\" task.");
            }
            else{
                notification.setNotification(user.getName() + " has accepted \"" + taskAssignment.getTask().getTaskName() + "\" task.");
            }
            notification.setTargetUrl("/user/" + taskAssignment.getTask().getCreator().getId() + "/task/" + taskAssignment.getTask().getId() + "/manage");
            notificationRepository.save(notification);
        }
        return true;
    }

    @SneakyThrows
    @Transactional
    public boolean abandonTaskAssignment(Integer taskId, Integer userId){
        Task task = checkTaskId(taskId);
        UserAccount user = checkUserId(userId);
        Optional<TaskAssignment> checkExist = taskAssignmentRepository.findById(new UserTaskPK(userId,taskId));
        if (checkExist.isEmpty()){
            return false;
        }
        TaskAssignment taskAssignment = checkExist.get();
        UserAccount currentAuthUser = (UserAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //Only owner of the task or executor of task can cancel the assignment
        if (!currentAuthUser.getId().equals(userId) && !currentAuthUser.getId().equals(task.getCreator().getId())){
            throw new NoPermissionException();
        }

        //If owner abandon subtask, remove main task
        if (task.getCreator().getId().equals(userId)){
            for (TaskAssignment assignment : task.getTaskAssignments()){
                if (!assignment.getTaskExecutor().getId().equals(task.getCreator().getId())) {
                    Notification notification = new Notification();
                    notification.setReceiver(assignment.getTaskExecutor());
                    notification.setRead(false);
                    notification.setNotification("\"" + task.getTaskName() + "\" task that you participated in has been deleted by manager.");
                    notification.setTime(LocalDateTime.now());
                    notificationRepository.save(notification);
                }
            }
            task.setTaskAssignments(null);
            taskAssignmentRepository.deleteAllByTask(task);
            taskRepository.delete(task);
            return true;
        }

        //Remove assignment in task
        List<TaskAssignment> taskAssignmentList = task.getTaskAssignments();
        taskAssignmentList.remove(taskAssignment);
        task.setTaskAssignments(taskAssignmentList);
        taskRepository.save(task);

        taskAssignmentList = user.getTaskAssignments();
        taskAssignmentList.remove(taskAssignment);
        user.setTaskAssignments(taskAssignmentList);
        userRepository.save(user);
        taskAssignmentRepository.deleteById(new UserTaskPK(userId,taskId));

        //Notify to owner of the task that a member has left the task
        Notification notification = new Notification();
        notification.setRead(false);
        notification.setTime(LocalDateTime.now());
        if (taskAssignment.getIsAccepted()) {
            notification.setNotification(user.getName() + " has left '" + task.getTaskName() + "' task");
        }
        else {
            notification.setNotification(user.getName() + " hasn't accepted to join '" + task.getTaskName() + "' task");
        }
        notification.setReceiver(task.getCreator());
        notification.setTargetUrl("/user/"+task.getCreator().getId()+"/task/"+task.getId()+"/manage");
        notificationRepository.save(notification);
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
