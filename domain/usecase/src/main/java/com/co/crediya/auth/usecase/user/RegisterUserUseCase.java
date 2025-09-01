package com.co.crediya.auth.usecase.user;

import static com.co.crediya.auth.usecase.util.validation.ReactiveValidators.*;
import static com.co.crediya.auth.usecase.util.validation.RoleValidator.isAnyRole;

import com.co.crediya.auth.model.user.User;
import com.co.crediya.auth.model.user.gateways.PasswordEncoder;
import com.co.crediya.auth.model.user.gateways.RoleRepository;
import com.co.crediya.auth.model.user.gateways.UserRepository;
import com.co.crediya.auth.usecase.constant.RoleType;
import com.co.crediya.auth.usecase.exception.ConflictException;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RegisterUserUseCase {
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;

  public Mono<User> execute(User user, UUID actorId) {
    return Mono.just(user)
        .flatMap(u -> validateActorRole(u, actorId))
        .flatMap(this::validateUserFields)
        .flatMap(this::validateUniqueEmail)
        .flatMap(this::attachDefaultRole)
        .flatMap(this::encodePassword)
        .flatMap(userRepository::saveUser);
  }

  private Mono<User> validateActorRole(User user, UUID actorId) {
    return roleRepository
        .findUserRole(actorId)
        .flatMap(role -> isAnyRole(role.getName(), RoleType.CONSULTANT, RoleType.ADMIN))
        .thenReturn(user);
  }
	
  private Mono<User> validateUniqueEmail(User user) {
    return userRepository
        .existsByEmail(user.getEmail())
        .flatMap(
            exists -> {
              if (Boolean.TRUE.equals(exists))
                return Mono.error(new ConflictException("Email already exists"));
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
