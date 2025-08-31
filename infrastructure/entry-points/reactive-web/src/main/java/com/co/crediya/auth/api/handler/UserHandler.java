package com.co.crediya.auth.api.handler;

import com.co.crediya.auth.api.dto.CreateUserDTO;
import com.co.crediya.auth.api.dto.UserResponseDTO;
import com.co.crediya.auth.api.mapper.UserMapper;
import com.co.crediya.auth.model.user.User;
import com.co.crediya.auth.model.user.gateways.TokenProvider;
import com.co.crediya.auth.usecase.user.UserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserHandler {
  private final UserUseCase userUseCase;
  private final TokenProvider tokenProvider;

  @Operation(
      operationId = "saveUser",
      summary = "Registra un nuevo usuario",
      requestBody =
          @RequestBody(content = @Content(schema = @Schema(implementation = CreateUserDTO.class))),
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Usuario creado con éxito",
            content = @Content(schema = @Schema(implementation = UserResponseDTO.class)))
      })
  public Mono<ServerResponse> listenPOSTUser(ServerRequest serverRequest) {
    return serverRequest
        .bodyToMono(CreateUserDTO.class)
        .map(UserMapper::toModel)
        .flatMap(userUseCase::saveUser)
        .flatMap(
            saved ->
                ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(
                        HttpHeaders.AUTHORIZATION,
                        tokenProvider.generateToken(
                            saved.getEmail(), Map.of("role", saved.getRole())))
                    .bodyValue(UserMapper.toResponse(saved)));
  }

  @Operation(
      operationId = "getUsers",
      summary = "Obtiene el listado de todos los usuarios",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de usuarios",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = User.class))))
      })
  public Mono<ServerResponse> listenGETAllUsers() {
    return ServerResponse.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(userUseCase.getUsers(), User.class);
  }

  public Mono<ServerResponse> listenPOSTLoginUser(ServerRequest serverRequest) {
    return null;
  }
}
