package com.co.crediya.auth.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.co.crediya.auth.api.handler.UserHandler;
import com.co.crediya.auth.api.routes.UserRoutes;
import com.co.crediya.auth.model.user.User;
import com.co.crediya.auth.usecase.user.LoginUseCase;
import com.co.crediya.auth.usecase.user.UserUseCase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest
@ContextConfiguration(classes = {RouterRest.class, UserHandler.class})
class RouterRestTest {

  @Autowired private WebTestClient webTestClient;
  @MockitoBean private UserUseCase userUseCase;
  @MockitoBean private LoginUseCase loginUseCase;

  @Test
  void listenPostUserTest() {
    User body = new User();
    when(userUseCase.saveUser(any(User.class))).thenReturn(Mono.just(body));
    webTestClient
        .post()
        .uri(UserRoutes.SIGN_UP_URL)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(body)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(User.class)
        .value(user -> Assertions.assertThat(user).isNotNull());
  }

  @Test
  void listenGetAllUsersTest() {
    User body = new User();
    when(userUseCase.getUsers()).thenReturn(Flux.just(body));
    webTestClient
        .get()
        .uri(UserRoutes.BASE_URL)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBodyList(User.class)
        .value(users -> Assertions.assertThat(users).isNotEmpty());
  }
}
