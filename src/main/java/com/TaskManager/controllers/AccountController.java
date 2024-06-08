package com.TaskManager.controllers;
import com.TaskManager.models.entities.UserAccount;
import com.TaskManager.services.AccountService;
import com.TaskManager.services.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
@RestController
public class AccountController {
    private final JwtService jwtService;

    private final AccountService accountService;

    public AccountController(JwtService jwtService, AccountService accountService) {
        this.jwtService = jwtService;
        this.accountService = accountService;
    }

    @GetMapping("/signup")
    public ResponseEntity<Void> register(@RequestBody UserAccount user) {
        accountService.signup(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/login")
    public ResponseEntity<String> authenticate(@RequestBody String data) {
        String username = data.split(";")[0];
        String pass = data.split(";")[1];
        UserAccount authenticatedUser = accountService.authenticate(username,pass);

        String jwtToken = jwtService.generateToken(authenticatedUser);


        return new ResponseEntity<>(jwtToken,HttpStatus.OK);
    }
}
