package com.co.crediya.auth.usecase.user;

import static com.co.crediya.auth.usecase.util.validation.ReactiveValidators.*;

import com.co.crediya.auth.model.user.User;
import com.co.crediya.auth.model.user.gateways.PasswordEncoder;
import com.co.crediya.auth.model.user.gateways.RoleRepository;
import com.co.crediya.auth.model.user.gateways.UserRepository;
import com.co.crediya.auth.usecase.exception.BusinessRuleException;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase {
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;

  public Mono<User> saveUser(User user) {
    return Mono.just(user)
        .flatMap(this::validateUserFields)
        .flatMap(this::validateUniqueEmail)
        .flatMap(this::attachDefaultRole)
        .flatMap(this::encodePassword)
        .flatMap(userRepository::saveUser);
  }

  public Flux<User> getUsers() {
    return userRepository.findAllUsers();
  }

  private Mono<User> validateUniqueEmail(User user) {
    return userRepository
        .existsByEmail(user.getEmail())
        .flatMap(
            exists -> {
              if (Boolean.TRUE.equals(exists))
                return Mono.error(new BusinessRuleException("Email already exists"));
              return Mono.just(user);
            });
  }

  private Mono<User> validateUserFields(User user) {
    return hasText(user.getName(), "Name")
        .then(hasText(user.getLastName(), "Last name"))
        .then(notNull(user.getBirthDate(), "Birth date"))
        .then(pastDate(user.getBirthDate(), "Birth date"))
        .then(hasText(user.getAddress(), "Address"))
        .then(hasText(user.getPhoneNumber(), "Phone number"))
        .then(email(user.getEmail()))
        .then(hasText(user.getPassword(), "Password"))
        .then(notNull(user.getBaseSalary(), "Base salary"))
        .then(
            range(
                user.getBaseSalary(),
                BigDecimal.ZERO,
                BigDecimal.valueOf(15000000.00),
                "Base salary"))
        .thenReturn(user);
  }

  private Mono<User> attachDefaultRole(User user) {
    return roleRepository
        .findDefaultRole()
        .map(
            role -> {
              user.setRole(role);
              return user;
            });
  }

  private Mono<User> encodePassword(User user) {
    String encodedPassword = passwordEncoder.encode(user.getPassword());
    user.setPassword(encodedPassword);
    return Mono.just(user);
  }
}
