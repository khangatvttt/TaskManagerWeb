package com.TaskManager.services;

import com.TaskManager.models.entities.UserAccount;
import com.TaskManager.repositories.UserRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.SneakyThrows;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class AccountService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JavaMailSender mailSender;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AccountService(UserRepository userRepository, UserService userService,
                          AuthenticationManager authenticationManager,
                          JavaMailSender mailSender, JwtService jwtService,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.mailSender = mailSender;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public void signup(UserAccount user, String baseURL) {
        userService.createUser(user, baseURL);
    }

    public UserAccount authenticate(String email, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        return userRepository.findByEmail(email)
                .orElseThrow();
    }

    public boolean verifyEmail(String code){
        Optional<UserAccount> userOpt = userRepository.findByVerificationCode(code);
        if (userOpt.isEmpty()){
            return false;
        }
        UserAccount user = userOpt.get();
        user.setVerificationCode(null);
        if (user.getActive()==null){
            user.setActive(true);
        }
        userRepository.save(user);
        return true;
    }

    @SneakyThrows
    public void requestResetPassword(Integer id, String baseURL){
        Optional<UserAccount> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()){
            return;
        }
        UserAccount user = userOpt.get();
        if (!user.getActive()){
            return;
        }

        String senderName = "Task Manager App";
        String from = "thanhlongfnd@gmail.com";
        String subject = "Reset your password";
        String content = "Dear [[name]],<br>"
                + "Someone (hopefully you) has requested a password reset for your account on Task Manager App. Follow the link below to set a new password:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">RESET PASSWORD</a></h3>"
                + "If you don't wish to reset your password, disregard this email and no action will be taken.<br>"
                + "Thank you!";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(from,senderName);
        helper.setTo(user.getEmail());
        helper.setSubject(subject);
        baseURL = "http://"+ baseURL;

        content = content.replace("[[name]]", user.getName());
        String verifyURL = baseURL + "/auth/reset-password?token=" + jwtService.generateResetPasswordToken(user);
        content = content.replace("[[URL]]", verifyURL);

        helper.setText(content, true);

        mailSender.send(message);

    }

    public boolean resetPassword(String token, String newPassword){
        if (newPassword.length()<6){
            return false;
        }
        String email = jwtService.extractUsername(token);
        UserAccount user = userRepository.findByEmail(email).orElseThrow();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }


}
