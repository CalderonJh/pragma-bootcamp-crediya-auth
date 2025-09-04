package com.co.crediya.auth.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.co.crediya.auth.api.config.TestSecurityConfig;
import com.co.crediya.auth.api.dto.CreateUserDTO;
import com.co.crediya.auth.api.dto.UserResponseDTO;
import com.co.crediya.auth.api.handler.UserHandler;
import com.co.crediya.auth.api.routes.UserRoutes;
import com.co.crediya.auth.model.user.Role;
import com.co.crediya.auth.model.user.User;
import com.co.crediya.auth.usecase.constant.RoleType;
import com.co.crediya.auth.usecase.user.FindUserUseCase;
import com.co.crediya.auth.usecase.user.LoginUseCase;
import com.co.crediya.auth.usecase.user.RegisterUserUseCase;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest
@ContextConfiguration(classes = {RouterRest.class, UserHandler.class, TestSecurityConfig.class})
class RouterRestTest {

  @Autowired private WebTestClient webTestClient;
  @MockitoBean private RegisterUserUseCase registerUserUseCase;
  @MockitoBean private LoginUseCase loginUseCase;
  @MockitoBean private FindUserUseCase findUserUseCase;
  public static final User userModel =
      User.builder()
          .id(UUID.randomUUID())
          .name("testuser")
          .password("password")
          .birthDate(LocalDate.now())
          .address("123 Test St")
          .phoneNumber("1234567890")
          .email("email@email.com")
          .baseSalary(BigDecimal.ONE)
          .role(new Role(UUID.randomUUID(), RoleType.USER.getValue()))
          .build();
  private final Jwt jwt =
      Jwt.withTokenValue("token")
          .subject(UUID.randomUUID().toString())
          .header("alg", "none")
          .claim("role", "user")
          .build();

  private final JwtAuthenticationToken authToken = new JwtAuthenticationToken(jwt);

  @Test
  void listenPostUserTest() {
    CreateUserDTO requestBody = new CreateUserDTO();
    when(registerUserUseCase.execute(any(User.class), any())).thenReturn(Mono.just(userModel));
    webTestClient
        .mutateWith(SecurityMockServerConfigurers.mockAuthentication(authToken))
        .post()
        .uri(UserRoutes.SIGN_UP_URL)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(requestBody)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(UserResponseDTO.class)
        .value(user -> Assertions.assertThat(user).isNotNull());
  }

  @Test
  void listenGetAllUsersTest() {

    when(findUserUseCase.getAll()).thenReturn(Flux.just(userModel));
    webTestClient
        .get()
        .uri(UserRoutes.BASE_URL)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBodyList(UserResponseDTO.class)
        .value(users -> Assertions.assertThat(users).isNotEmpty());
  }
}
