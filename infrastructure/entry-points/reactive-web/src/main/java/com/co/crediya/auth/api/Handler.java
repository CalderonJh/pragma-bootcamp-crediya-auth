package com.co.crediya.auth.api;

import com.co.crediya.auth.model.user.User;
import com.co.crediya.auth.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {
  private final UserUseCase userUseCase;

  public Mono<ServerResponse> listenPOSTUser(ServerRequest serverRequest) {
    return serverRequest
        .bodyToMono(User.class)
        .flatMap(userUseCase::saveUser)
        .flatMap(
            saved -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(saved));
  }
}
