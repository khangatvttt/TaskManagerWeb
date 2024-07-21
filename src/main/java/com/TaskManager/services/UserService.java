package com.TaskManager.services;

import com.TaskManager.models.dto.*;
import com.TaskManager.models.entities.Task;
import com.TaskManager.models.entities.TaskAssignment;
import com.TaskManager.models.entities.UserAccount;
import com.TaskManager.repositories.TaskAssignmentRepository;
import com.TaskManager.repositories.TaskRepository;
import com.TaskManager.repositories.UserRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.SneakyThrows;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.NoPermissionException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TaskAssignmentRepository taskAssignmentRepository;
    private final JavaMailSender mailSender;

    public UserService(TaskRepository taskRepository, UserRepository userRepository,
                       TaskAssignmentRepository taskAssignmentRepository, PasswordEncoder passwordEncoder,
                       JavaMailSender mailSender) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.taskAssignmentRepository = taskAssignmentRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
    }

    public void createUser(UserAccount userAccount, String baseURL){
        if (userRepository.existsByEmail(userAccount.getEmail())){
            throw new DuplicateKeyException("This email have been used");
        }
        userAccount.setActive(false);
        userAccount.setVerificationCode(UUID.randomUUID().toString());
        userAccount.setPassword(passwordEncoder.encode(userAccount.getPassword()));
        sendVerificationEmail(userAccount,baseURL);
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
        if (updateUser.getPassword()!=null){
            updateUser.setPassword(passwordEncoder.encode(updateUser.getPassword()));
        }
        user.merge(updateUser);
        userRepository.save(user);
    }

    public List<TaskDto> getTasksByUser(Integer userid){
        UserAccount user = checkUserId(userid);
        checkPermission(user);
        List<TaskAssignment> taskAssignmentList = user.getTaskAssignments();
        return taskAssignmentList.stream()
                .map(TaskAssignment::getTask)
                .map(TaskMapper::toTaskDto)
                .toList();
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

    @SneakyThrows
    private void sendVerificationEmail(UserAccount user, String baseURL) {
        String senderName = "Task Manager App";
        String from = "thanhlongfnd@gmail.com";
        String subject = "Verify your registration";
        String content = "Dear [[name]],<br>"
                + "Please click the link below to verify your registration:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                + "Thank you!";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(from,senderName);
        helper.setTo(user.getEmail());
        helper.setSubject(subject);
        baseURL = "http://"+ baseURL;

        content = content.replace("[[name]]", user.getName());
        String verifyURL = baseURL + "/auth/verify?code=" + user.getVerificationCode();
        content = content.replace("[[URL]]", verifyURL);

        helper.setText(content, true);

        mailSender.send(message);

    }

}
