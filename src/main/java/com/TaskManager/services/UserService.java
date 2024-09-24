package com.TaskManager.services;

import com.TaskManager.models.dto.*;
import com.TaskManager.models.entities.Notification;
import com.TaskManager.models.entities.Task;
import com.TaskManager.models.entities.TaskAssignment;
import com.TaskManager.models.entities.UserAccount;
import com.TaskManager.repositories.NotificationRepository;
import com.TaskManager.repositories.TaskAssignmentRepository;
import com.TaskManager.repositories.TaskRepository;
import com.TaskManager.repositories.UserRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.SneakyThrows;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.NoPermissionException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;


@Service
public class UserService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TaskAssignmentRepository taskAssignmentRepository;
    private final JavaMailSender mailSender;
    private final ImageService imageService;
    private final NotificationRepository notificationRepository;

    public UserService(TaskRepository taskRepository, UserRepository userRepository,
                       TaskAssignmentRepository taskAssignmentRepository, PasswordEncoder passwordEncoder,
                       JavaMailSender mailSender, ImageService imageService, NotificationRepository notificationRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.taskAssignmentRepository = taskAssignmentRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
        this.imageService = imageService;
        this.notificationRepository = notificationRepository;
    }

    public void createUser(UserAccount userAccount){
        Optional<UserAccount> userOpt = userRepository.findByEmail(userAccount.getEmail());
        if (userOpt.isPresent()) {
            if (userOpt.get().getActive()) {
                throw new DuplicateKeyException("This email have been used");
            }
            else {
                userRepository.delete(userOpt.get());
            }
        }
        userAccount.setActive(false);
        userAccount.setVerificationCode(UUID.randomUUID().toString());
        userAccount.setPassword(passwordEncoder.encode(userAccount.getPassword()));
        userAccount.setProfilePicture("https://firebasestorage.googleapis.com/v0/b/task-manager-1eddc.appspot.com/o/defaultAvatar.jpg?alt=media&token=e7440e47-2b7a-4bba-b062-575b0c0443e9"); //default avatar
        userAccount.setCreateAt(LocalDateTime.now());
        sendVerificationEmail(userAccount);
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

    public String updateUser(UserAccount updateUser, Integer userId, MultipartFile image){
        UserAccount user = checkUserId(userId);
        checkPermission(user);
        if (image!=null) {
            String uploadedImage = imageService.upload(image);
            if (uploadedImage==null){
                return "Fail to upload image. Your file is not valid image file or it may have a problem in connection";
            }
            updateUser.setProfilePicture(uploadedImage);
            //delete the old picture to release spaces
            imageService.deleteImage(user.getProfilePicture());
        }
        if (updateUser.getPassword()!=null){
            updateUser.setPassword(passwordEncoder.encode(updateUser.getPassword()));
        }
        user.merge(updateUser);
        userRepository.save(user);
        return "OK";
    }

    public List<TaskAssignmentDto> getTasksByUser(Integer userId){
        UserAccount user = checkUserId(userId);
        checkPermission(user);
        List<TaskAssignment> taskAssignmentList = user.getTaskAssignments();
        return taskAssignmentList.stream()
                .map(TaskMapper::toTaskAssignmentDto)
                .toList();
    }

    public TaskSummaryDto getTaskSummary(Integer userId){
        UserAccount user = checkUserId(userId);
        checkPermission(user);
        return new TaskSummaryDto(user.getTaskAssignments().size()
                ,taskRepository.countByCreator(user)
                ,taskAssignmentRepository.countByTaskExecutorAndStatus(user, Task.Status.INPROGRESS)
                ,taskAssignmentRepository.countByTaskExecutorAndStatus(user, Task.Status.COMPLETED));
    }

    public Page<Notification> getLatestNotifications(Integer userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        UserAccount user = checkUserId(userId);
        return notificationRepository.findByReceiverOrderByTimeDesc(user, pageable);
    }

    public Integer countUnreadNotification(Integer userId){
        UserAccount user = checkUserId(userId);
        return notificationRepository.countByIsReadAndReceiver(false, user);
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
    private void sendVerificationEmail(UserAccount user) {
        String baseURL = "localhost:3000";
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
        String verifyURL = baseURL + "/verify?code=" + user.getVerificationCode();
        content = content.replace("[[URL]]", verifyURL);

        helper.setText(content, true);

        mailSender.send(message);

    }

}
