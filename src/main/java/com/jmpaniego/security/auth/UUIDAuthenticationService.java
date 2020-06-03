package com.jmpaniego.security.auth;

import com.jmpaniego.security.models.User;
import com.jmpaniego.security.repositories.UserCrud;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service("uuid")
public class UUIDAuthenticationService implements UserAuthenticationService {
  @Autowired
  UserCrud users;

  @Autowired
  PasswordEncoder passwordEncoder;

  @Override
  public Optional<String> login(String username, String password) {
    final String uuid = UUID.randomUUID().toString();
    //final User user = User.builder().id(uuid).username(username).password(password).build();
    User user = new User(uuid,username,passwordEncoder.encode(password));
    users.save(user);
    return Optional.of(uuid);
  }

  @Override
  public Optional<User> findByToken(String token) {
    return users.find(token);
  }

  @Override
  public void logout(User user) {
    // nothing TODO
  }
}
