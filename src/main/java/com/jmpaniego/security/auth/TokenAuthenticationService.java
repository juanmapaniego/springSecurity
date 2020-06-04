package com.jmpaniego.security.auth;

import com.google.common.collect.ImmutableMap;
import com.jmpaniego.security.config.TokenService;
import com.jmpaniego.security.models.User;
import com.jmpaniego.security.repositories.UserCrud;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

import static java.util.Optional.of;

@Service("token")
public class TokenAuthenticationService implements UserAuthenticationService {

  @Autowired
  TokenService tokenService;
  @Autowired
  UserCrud userCrud;
  @Autowired
  PasswordEncoder passwordEncoder;

  @Override
  public Optional<String> login(String username, String password) {
    return userCrud
        .findByUsername(username)
        //.filter(user -> Objects.equals(password, user.getPassword()))
        .filter(user -> passwordEncoder.matches(password, user.getPassword()))
        .map(user -> tokenService.expiring(ImmutableMap.of("username",username)));
  }

  @Override
  public Optional<User> findByToken(String token) {
    return of(tokenService.verify(token))
        .map(map -> map.get("username"))
        .flatMap(userCrud::findByUsername);
  }

  @Override
  public void logout(User user) {
    //Nothing to do
  }
}
