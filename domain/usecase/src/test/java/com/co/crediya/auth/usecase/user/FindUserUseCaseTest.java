package com.co.crediya.auth.usecase.user;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.co.crediya.auth.model.user.User;
import com.co.crediya.auth.model.user.gateways.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class FindUserUseCaseTest {
  private UserRepository userRepository;
  private FindUserUseCase useCase;
  private User user;

  @BeforeEach
  void setUp() {
    userRepository = mock(UserRepository.class);
    useCase = new FindUserUseCase(userRepository);
    user =
        User.builder()
            .name("name")
            .lastName("lastName")
            .birthDate(LocalDate.of(1990, 1, 1))
            .address("address")
            .phoneNumber("1234567890")
            .email("email@email.com")
            .baseSalary(BigDecimal.valueOf(1000000))
            .password("securepassword123")
            .build();
  }

  @Test
  @DisplayName("Find all users test")
  void findAllUsersTest() {
    when(userRepository.findAllUsers()).thenReturn(Flux.just(user));
    StepVerifier.create(useCase.getAll()).expectNextCount(1).verifyComplete();
  }

  @Test
  @DisplayName("Get user by id test")
  void getUserByIdTest() {
    UUID userId = UUID.randomUUID();
    when(userRepository.findById(userId)).thenReturn(Mono.just(user));
    StepVerifier.create(useCase.getById(userId)).expectNext(user).verifyComplete();
  }
}
