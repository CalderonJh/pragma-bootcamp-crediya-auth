package com.co.crediya.auth.usecase.user;

import static com.co.crediya.auth.usecase.util.ValidationUtils.*;

import com.co.crediya.auth.model.exception.BusinessRuleException;
import com.co.crediya.auth.model.role.gateways.RoleRepository;
import com.co.crediya.auth.model.user.User;
import com.co.crediya.auth.model.user.gateways.UserRepository;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase {
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;

  public Mono<User> saveUser(User user) {
    return Mono.just(user)
        .flatMap(this::validateUserFields)
        .flatMap(this::validateUniqueEmail)
        .flatMap(this::attachDefaultRole)
        .flatMap(userRepository::saveUser);
  }

  private Flux<User> getUsers() {
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
    if (!hasText(user.getName()))
      return Mono.error(new BusinessRuleException("Name cannot be blank"));
    if (!hasText(user.getLastName()))
      return Mono.error(new BusinessRuleException("Last name cannot be blank"));
    if (isNull(user.getBirthDate()))
      return Mono.error(new BusinessRuleException("Birth date cannot be null"));
    if (!hasText(user.getAddress()))
      return Mono.error(new BusinessRuleException("Address cannot be blank"));
    if (!hasText(user.getPhoneNumber()))
      return Mono.error(new BusinessRuleException("Phone number cannot be blank"));
    if (!isValidEmail(user.getEmail()))
      return Mono.error(new BusinessRuleException("Email is invalid"));
    if (isNull(user.getBaseSalary())
        || !inRange(user.getBaseSalary(), BigDecimal.ZERO, BigDecimal.valueOf(15000000.00)))
      return Mono.error(
          new BusinessRuleException("Base salary must be a positive number up to 15,000,000.00"));
    return Mono.just(user);
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
}
