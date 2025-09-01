package com.co.crediya.auth.usecase.user;

import static com.co.crediya.auth.usecase.constant.Constant.MAX_LOGIN_ATTEMPTS;
import static com.co.crediya.auth.usecase.util.validation.ReactiveValidators.hasText;

import com.co.crediya.auth.model.user.LoginResult;
import com.co.crediya.auth.model.user.User;
import com.co.crediya.auth.model.user.gateways.PasswordEncoder;
import com.co.crediya.auth.model.user.gateways.TokenProvider;
import com.co.crediya.auth.model.user.gateways.UserRepository;
import com.co.crediya.auth.usecase.exception.ConflictException;
import com.co.crediya.auth.usecase.exception.DataNotFoundException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoginUseCase {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final TokenProvider tokenProvider;

  public Mono<LoginResult> login(String email, String password) {
    return hasText(email, "Username")
        .then(hasText(password, "Password"))
        .then(Mono.defer(() -> userRepository.findByEmail(email)))
        .switchIfEmpty(Mono.error(new DataNotFoundException("User with email was not found")))
        .flatMap(this::validateFailedLoginAttempts)
        .flatMap(user -> validatePassword(user, password));
  }

  private Mono<User> validateFailedLoginAttempts(User user) {
    int userAttempts = user.getFailedLoginAttempts();

    if (userAttempts >= MAX_LOGIN_ATTEMPTS)
      return Mono.error(
          new ConflictException(
              "User account is locked due to too many failed login attempts"));

    return Mono.just(user);
  }

  private Mono<LoginResult> validatePassword(User user, String rawPassword) {
    boolean isPasswordCorrect = passwordEncoder.matches(rawPassword, user.getPassword());
    if (!isPasswordCorrect) user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
    else user.setFailedLoginAttempts(0);
    return userRepository
        .saveUser(user)
        .flatMap(
            saved ->
                Mono.just(
                    new LoginResult(
                        isPasswordCorrect,
                        saved.getFailedLoginAttempts(),
                        MAX_LOGIN_ATTEMPTS,
                        isPasswordCorrect
                            ? tokenProvider.generateToken(
                                saved.getId().toString(), Map.of("role", saved.getRole()))
                            : null)));
  }
}
