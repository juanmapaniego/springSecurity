package com.jmpaniego.security.controllers;

import com.jmpaniego.security.auth.UserAuthenticationService;
import com.jmpaniego.security.models.User;
import com.jmpaniego.security.repositories.UserCrud;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/public/users")
@FieldDefaults(level = PRIVATE, makeFinal = true)
@AllArgsConstructor(access = PACKAGE)
public class PublicUsersController {
    @NonNull
    UserAuthenticationService authentication;
    @NonNull
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
        users.save(User.builder().id(username).username(username).password(encoded).build());
        System.out.printf("%s,%s,%s",username,password,encoded);
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
