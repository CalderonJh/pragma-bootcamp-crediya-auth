package com.co.crediya.auth.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.co.crediya.auth.api.config.path.UserPath;
import com.co.crediya.auth.model.user.User;
import com.co.crediya.auth.usecase.user.UserUseCase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@WebFluxTest
@ContextConfiguration(classes = {RouterRest.class, Handler.class})
@EnableConfigurationProperties(UserPath.class)
class RouterRestTest {

  @Autowired private WebTestClient webTestClient;
  @MockitoBean private UserUseCase userUseCase;

  @Test
  void listenPostUserTest() {
    User body = new User();
    when(userUseCase.saveUser(any(User.class))).thenReturn(Mono.just(body));
    webTestClient
        .post()
        .uri("/api/v1/usuarios")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(body)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(User.class)
        .value(user -> Assertions.assertThat(user).isNotNull());
  }
}
