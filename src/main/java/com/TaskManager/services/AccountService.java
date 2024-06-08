package com.TaskManager.services;

import com.TaskManager.models.entities.UserAccount;
import com.TaskManager.repositories.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

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

    public void signup(UserAccount user) {
        userService.createUser(user);
    }

    public UserAccount authenticate(String email, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        return userRepository.findByEmail(email)
                .orElseThrow();
    }
}
