package com.TaskManager.services;

import com.TaskManager.models.entities.UserAccount;
import com.TaskManager.repositories.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public AccountService(UserRepository userRepository, UserService userService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.authenticationManager = authenticationManager;
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
}
