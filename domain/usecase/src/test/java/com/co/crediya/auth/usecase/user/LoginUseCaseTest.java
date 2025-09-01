package com.co.crediya.auth.usecase.user;

import static com.co.crediya.auth.usecase.constant.Constant.MAX_LOGIN_ATTEMPTS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.co.crediya.auth.model.user.Role;
import com.co.crediya.auth.model.user.User;
import com.co.crediya.auth.model.user.gateways.PasswordEncoder;
import com.co.crediya.auth.model.user.gateways.TokenProvider;
import com.co.crediya.auth.model.user.gateways.UserRepository;
import com.co.crediya.auth.usecase.exception.ConflictException;
import com.co.crediya.auth.usecase.exception.ValidationException;
import com.co.crediya.auth.usecase.util.validation.ReactiveValidators;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class LoginUseCaseTest {
  private UserRepository userRepository;
  private PasswordEncoder passwordEncoder;
  private TokenProvider tokenProvider;
  private LoginUseCase useCase;
  private User user;

  @BeforeEach
  void setUp() {
    userRepository = mock(UserRepository.class);
    passwordEncoder = mock(PasswordEncoder.class);
    tokenProvider = mock(TokenProvider.class);
    useCase = new LoginUseCase(userRepository, passwordEncoder, tokenProvider);
    user =
        User.builder()
            .id(UUID.randomUUID())
            .name("name")
            .lastName("lastName")
            .birthDate(LocalDate.of(1990, 1, 1))
            .address("address")
            .phoneNumber("1234567890")
            .email("email@email.com")
            .baseSalary(BigDecimal.valueOf(1000000))
            .password("securepassword123")
            .failedLoginAttempts(0)
            .role(new Role(UUID.randomUUID(), "USER"))
            .build();
  }

  @Test
  @DisplayName("Error if username is blank")
  void errorIfUsernameIsBlank() {
    StepVerifier.create(useCase.login("", "somepassword"))
        .expectErrorSatisfies(
            e ->
                assertThat(e)
                    .isInstanceOf(ValidationException.class)
                    .hasMessage(ReactiveValidators.MessageTemplate.NOT_EMPTY.render("Username")))
        .verify();
  }

  @Test
  @DisplayName("Error if password is blank")
  void errorIfPasswordIsBlank() {
    StepVerifier.create(useCase.login("email@email.com", null))
        .expectErrorSatisfies(
            e ->
                assertThat(e)
                    .isInstanceOf(ValidationException.class)
                    .hasMessage(ReactiveValidators.MessageTemplate.NOT_EMPTY.render("Password")))
        .verify();
  }

  @Test
  @DisplayName("Error if account is blocked due to too many failed login attempts")
  void errorIfAccountIsBlocked() {
    user.setFailedLoginAttempts(MAX_LOGIN_ATTEMPTS);
    when(userRepository.findByEmail(user.getEmail())).thenReturn(Mono.just(user));
    StepVerifier.create(useCase.login(user.getEmail(), user.getPassword()))
        .expectErrorSatisfies(e -> assertThat(e).isInstanceOf(ConflictException.class))
        .verify();
  }

  @Test
  @DisplayName("Successful login")
  void loginTest() {
    String email = user.getEmail();
    String rawPassword = "securepassword123";
    String token = "jwt-token";

    when(userRepository.findByEmail(email)).thenReturn(Mono.just(user));
    when(passwordEncoder.matches(rawPassword, user.getPassword())).thenReturn(true);
    when(userRepository.saveUser(any(User.class)))
        .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
    when(tokenProvider.generateToken(eq(user.getId().toString()), anyMap())).thenReturn(token);

    StepVerifier.create(useCase.login(email, rawPassword))
        .assertNext(
            result -> {
              assertThat(result.isSuccess()).isTrue();
              assertThat(result.getFailedAttempts()).isZero();
              assertThat(result.getMaxAttempts()).isEqualTo(MAX_LOGIN_ATTEMPTS);
              assertThat(result.getToken()).isEqualTo(token);
            })
        .verifyComplete();

    verify(userRepository).findByEmail(email);
    verify(passwordEncoder).matches(rawPassword, user.getPassword());
    verify(userRepository).saveUser(any(User.class));
    verify(tokenProvider).generateToken(eq(user.getId().toString()), anyMap());
  }
}
