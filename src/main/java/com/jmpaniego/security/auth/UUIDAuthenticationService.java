package com.jmpaniego.security.auth;

import com.jmpaniego.security.models.User;
import com.jmpaniego.security.repositories.UserCrud;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

@Service
@AllArgsConstructor(access = PACKAGE)
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class UUIDAuthenticationService implements UserAuthenticationService {
  @NonNull
  UserCrud users;

  @Override
  public Optional<String> login(String username, String password) {
    final String uuid = UUID.randomUUID().toString();
    final User user = User.builder().id(uuid).username(username).password(password).build();

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
