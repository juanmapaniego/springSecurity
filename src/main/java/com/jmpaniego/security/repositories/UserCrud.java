package com.jmpaniego.security.repositories;

import com.jmpaniego.security.models.User;

import java.util.Optional;

public interface UserCrud {
    User save(User user);

    Optional<User> find(String id);

    Optional<User> findByUsername(String username);
}
