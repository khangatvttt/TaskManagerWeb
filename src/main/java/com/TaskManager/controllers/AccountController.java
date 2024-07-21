package com.TaskManager.controllers;
import com.TaskManager.models.dto.LoginDto;
import com.TaskManager.models.entities.UserAccount;
import com.TaskManager.services.AccountService;
import com.TaskManager.services.JwtService;
import jakarta.servlet.http.HttpServletRequest;
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
    public ResponseEntity<Void> register(HttpServletRequest request, @RequestBody UserAccount user) {
        accountService.signup(user, getBaseURL(request));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<String> authenticate(@RequestBody LoginDto loginInfo) {
        UserAccount authenticatedUser = accountService.authenticate(loginInfo.email(), loginInfo.password());

        String jwtToken = jwtService.generateLoginToken(authenticatedUser);


        return new ResponseEntity<>(jwtToken, HttpStatus.OK);
    }

    @GetMapping("/verify")
    public ResponseEntity<Void> verifyEmail(@RequestParam("code") String code) {
        boolean success = accountService.verifyEmail(code);
        return new ResponseEntity<>(success?HttpStatus.OK:HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/forget-password")
    public ResponseEntity<Void> forgetPassword(HttpServletRequest request, @RequestParam("id") Integer id) {
        accountService.requestResetPassword(id, getBaseURL(request));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private String getBaseURL(HttpServletRequest request){
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String baseURL;
        if ((serverPort == 80 && request.getScheme().equals("http")) ||
                (serverPort == 443 && request.getScheme().equals("https"))) {
            baseURL = serverName;
        } else {
            baseURL = serverName + ":" + serverPort;
        }
        return baseURL;
    }
}
