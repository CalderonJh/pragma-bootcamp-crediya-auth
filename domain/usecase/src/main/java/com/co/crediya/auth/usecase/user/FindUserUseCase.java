package com.co.crediya.auth.usecase.user;

import com.co.crediya.auth.model.user.User;
import com.co.crediya.auth.model.user.gateways.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class FindUserUseCase {
  private final UserRepository userRepository;

  public Flux<User> getAll() {
    return userRepository.findAllUsers();
  }

  public Mono<User> getById(UUID id) {
    return userRepository.findById(id);
  }
}
