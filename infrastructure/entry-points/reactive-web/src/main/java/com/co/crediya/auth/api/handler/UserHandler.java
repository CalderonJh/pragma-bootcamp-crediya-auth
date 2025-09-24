package com.co.crediya.auth.api.handler;

import static com.co.crediya.auth.usecase.util.validation.ValidationUtils.hasText;

import com.co.crediya.auth.api.dto.*;
import com.co.crediya.auth.api.mapper.UserMapper;
import com.co.crediya.auth.usecase.constant.RoleType;
import com.co.crediya.auth.usecase.user.FindUserUseCase;
import com.co.crediya.auth.usecase.user.LoginUseCase;
import com.co.crediya.auth.usecase.user.RegisterUserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Tag(name = "User operations")
public class UserHandler {
  private final RegisterUserUseCase registerUserUseCase;
  private final LoginUseCase loginUseCase;
  private final FindUserUseCase findUserUseCase;

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
        .principal()
        .cast(JwtAuthenticationToken.class)
        .flatMap(
            auth -> {
              Jwt jwt = auth.getToken();
              UUID actorId = UUID.fromString(jwt.getSubject());
              return serverRequest
                  .bodyToMono(CreateUserDTO.class)
                  .map(UserMapper::toModel)
                  .flatMap(user -> registerUserUseCase.execute(user, actorId))
                  .flatMap(
                      saved ->
                          ServerResponse.ok()
                              .contentType(MediaType.APPLICATION_JSON)
                              .bodyValue(UserMapper.toResponse(saved)));
            })
        .switchIfEmpty(ServerResponse.status(HttpStatus.UNAUTHORIZED).build());
  }

  @Operation(
      operationId = "login",
      summary = "Inicia sesión de un usuario",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Si el login es exitoso",
            content =
                @Content(array = @ArraySchema(schema = @Schema(implementation = LoginDTO.class)))),
        @ApiResponse(
            responseCode = "401",
            description = "Si el login falla",
            content =
                @Content(
                    array = @ArraySchema(schema = @Schema(implementation = LoginResultDTO.class))))
      })
  public Mono<ServerResponse> listenPOSTLoginUser(ServerRequest serverRequest) {
    return serverRequest
        .bodyToMono(LoginDTO.class)
        .flatMap(login -> loginUseCase.login(login.getEmail(), login.getPassword()))
        .flatMap(
            result -> {
              HttpStatus status = result.isSuccess() ? HttpStatus.OK : HttpStatus.UNAUTHORIZED;
              ServerResponse.BodyBuilder builder =
                  ServerResponse.status(status).contentType(MediaType.APPLICATION_JSON);

              if (hasText(result.getToken()))
                builder.header(HttpHeaders.AUTHORIZATION, result.getToken());

              return builder.bodyValue(
                  new LoginResultDTO(
                      result.isSuccess(), result.getFailedAttempts(), result.getMaxAttempts()));
            });
  }

  @Operation(
      operationId = "getUsers",
      summary = "Obtiene el listado de todos los usuarios",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de usuarios",
            content =
                @Content(
                    array = @ArraySchema(schema = @Schema(implementation = UserResponseDTO.class))))
      })
  public Mono<ServerResponse> listenGETAllUsers() {
    return ServerResponse.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(findUserUseCase.getAll().map(UserMapper::toResponse), UserResponseDTO.class);
  }

  @Operation(
      operationId = "getUserById",
      summary = "Obtiene información de un usuario por su ID",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Usuario encontrado",
            content =
                @Content(
                    array = @ArraySchema(schema = @Schema(implementation = UserResponseDTO.class))))
      })
  public Mono<ServerResponse> listenGETUserById(ServerRequest serverRequest) {
    UUID userId = UUID.fromString(serverRequest.pathVariable("id"));
    return ServerResponse.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(findUserUseCase.getById(userId).map(UserMapper::toResponse), UserResponseDTO.class);
  }

  @Operation(
      operationId = "getUserByIdIn",
      summary = "Obtiene información de varios usuarios por sus IDs",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Usuarios encontrado",
            content =
                @Content(
                    array = @ArraySchema(schema = @Schema(implementation = UserResponseDTO.class))))
      })
  public Mono<ServerResponse> listenGETUserByIdIn(ServerRequest serverRequest) {
    return serverRequest
        .bodyToMono(UsersIdsDTO.class)
        .map(UsersIdsDTO::ids)
        .flatMap(
            ids -> {
              if (ids == null || ids.isEmpty()) {
                return ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Mono.just(Set.of()), UserResponseDTO.class);
              } else {
                return ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(
                        findUserUseCase.getByIdIn(ids).map(UserMapper::toResponse),
                        UserResponseDTO.class);
              }
            });
  }

  @Operation(
      operationId = "getUserByRole",
      summary = "Obtiene todos los usuarios que tienen un rol específico",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Usuarios encontrado",
            content =
                @Content(
                    array = @ArraySchema(schema = @Schema(implementation = UserResponseDTO.class))))
      })
  public Mono<ServerResponse> listenGETUserByRole(ServerRequest serverRequest) {
    RoleType role = serverRequest.queryParam("rol").map(RoleType::fromString).orElse(RoleType.USER);
    return ServerResponse.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(findUserUseCase.getByRole(role).map(UserMapper::toResponse), UserResponseDTO.class);
  }
}
