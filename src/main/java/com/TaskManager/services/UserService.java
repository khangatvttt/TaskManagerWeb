package com.TaskManager.services;

import com.TaskManager.models.dto.TaskAssignmentDto;
import com.TaskManager.models.dto.TaskMapper;
import com.TaskManager.models.dto.UserDto;
import com.TaskManager.models.dto.UserMapper;
import com.TaskManager.models.entities.Task;
import com.TaskManager.models.entities.TaskAssignment;
import com.TaskManager.models.entities.UserAccount;
import com.TaskManager.repositories.TaskAssignmentRepository;
import com.TaskManager.repositories.TaskRepository;
import com.TaskManager.repositories.UserRepository;
import com.sun.source.util.TaskListener;
import jakarta.validation.Valid;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskAssignmentRepository taskAssignmentRepository;

    public UserService(TaskRepository taskRepository, UserRepository userRepository,
                       TaskAssignmentRepository taskAssignmentRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.taskAssignmentRepository = taskAssignmentRepository;
    }

    public void createUser(UserAccount userAccount){
        if (userRepository.existsByEmail(userAccount.getEmail())){
            throw new DuplicateKeyException("This email have been used");
        }
        userRepository.save(userAccount);
    }

    public void deleteUser(Integer userId){
        UserAccount user = checkUserId(userId);
        userRepository.delete(user);
    }

    public UserDto getUserInfo(Integer userId){
        UserAccount user = checkUserId(userId);
        return UserMapper.toUserDto(user);
    }

    public void updateUser(UserAccount updateUser, Integer userId){
        UserAccount user = checkUserId(userId);
        user.merge(updateUser);
        userRepository.save( user);
    }

    public List<TaskAssignmentDto> getTasksByUser(Integer userid){
        UserAccount user = checkUserId(userid);
        List<TaskAssignment> taskAssignmentList = user.getTaskAssignments();
        return taskAssignmentList.stream()
                .map(TaskMapper::toTaskAssignmentDto)
                .collect(Collectors.toList());
    }

    public UserAccount checkUserId(Integer userId){
        Optional<UserAccount> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()){
            throw new NoSuchElementException("User with id {"+userId+"} doesn't exist");
        }
        return userOpt.get();
    }

}
