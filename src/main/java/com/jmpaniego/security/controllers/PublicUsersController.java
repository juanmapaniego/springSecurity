package com.jmpaniego.security.controllers;

import com.jmpaniego.security.auth.UserAuthenticationService;
import com.jmpaniego.security.models.User;
import com.jmpaniego.security.repositories.UserCrud;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public/users")
public class PublicUsersController {
    @Autowired
        @Qualifier("token")
    UserAuthenticationService authentication;
    @Autowired
    UserCrud users;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(10);
    }

    @PostMapping("/register")
    String register(
            @RequestParam("username") final String username,
            @RequestParam("password") final String password) {

        String encoded = passwordEncoder().encode(password);
        users.save(new User(username,username,passwordEncoder().encode(password)));
        return login(username, password);
    }

    @PostMapping("/login")
    String login(
            @RequestParam("username") final String username,
            @RequestParam("password") final String password) {
        return authentication
                .login(username, password)
                .orElseThrow(() -> new RuntimeException("invalid login and/or password"));
    }

    @GetMapping
    String home(){
        return "index";
    }
}
