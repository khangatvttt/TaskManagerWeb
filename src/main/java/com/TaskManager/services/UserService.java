package com.TaskManager.services;

import com.TaskManager.models.dto.TaskAssignmentDto;
import com.TaskManager.models.dto.TaskMapper;
import com.TaskManager.models.dto.UserDto;
import com.TaskManager.models.dto.UserMapper;
import com.TaskManager.models.entities.TaskAssignment;
import com.TaskManager.models.entities.UserAccount;
import com.TaskManager.repositories.TaskAssignmentRepository;
import com.TaskManager.repositories.TaskRepository;
import com.TaskManager.repositories.UserRepository;
import lombok.SneakyThrows;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.NoPermissionException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TaskAssignmentRepository taskAssignmentRepository;

    public UserService(TaskRepository taskRepository, UserRepository userRepository,
                       TaskAssignmentRepository taskAssignmentRepository, PasswordEncoder passwordEncoder) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.taskAssignmentRepository = taskAssignmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void createUser(UserAccount userAccount){
        if (userRepository.existsByEmail(userAccount.getEmail())){
            throw new DuplicateKeyException("This email have been used");
        }
        userAccount.setPassword(passwordEncoder.encode(userAccount.getPassword()));
        userRepository.save(userAccount);
    }

    public void deleteUser(Integer userId){
        UserAccount user = checkUserId(userId);
        checkPermission(user);
        userRepository.delete(user);
    }

    public UserDto getUserInfo(Integer userId){
        UserAccount user = checkUserId(userId);
        checkPermission(user);
        return UserMapper.toUserDto(user);
    }

    public void updateUser(UserAccount updateUser, Integer userId){
        UserAccount user = checkUserId(userId);
        checkPermission(user);
        user.merge(updateUser);
        userRepository.save(user);
    }

    public List<TaskAssignmentDto> getTasksByUser(Integer userid){
        UserAccount user = checkUserId(userid);
        checkPermission(user);
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

    @SneakyThrows
    public void checkPermission(UserAccount owner){
        UserAccount currentUser = (UserAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!currentUser.getId().equals(owner.getId())) {
            throw new NoPermissionException();
        }

    }

}
