package com.co.crediya.auth.api;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import com.co.crediya.auth.model.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@RequiredArgsConstructor
public class RouterRest {
  @RouterOperations({
    @RouterOperation(
        path = "/api/v1/usuarios",
        method = RequestMethod.POST,
        beanClass = Handler.class,
        beanMethod = "listenPOSTUser",
        operation =
            @Operation(
                operationId = "saveUser",
                summary = "Registra un nuevo usuario",
                requestBody =
                    @RequestBody(content = @Content(schema = @Schema(implementation = User.class))),
                responses = {
                  @ApiResponse(
                      responseCode = "200",
                      content = @Content(schema = @Schema(implementation = User.class))),
                })),
    @RouterOperation(
        path = "/api/v1/usuarios",
        method = RequestMethod.GET,
        beanClass = Handler.class,
        beanMethod = "listenGETAllUsers",
        operation =
            @Operation(
                operationId = "getUsers",
                summary = "Obtiene el listado de todos los usuarios",
                requestBody = @RequestBody(content = @Content()),
                responses = {
                  @ApiResponse(
                      responseCode = "200",
                      content =
                          @Content(
                              array = @ArraySchema(schema = @Schema(implementation = User.class)))),
                }))
  })
  @Bean
  public RouterFunction<ServerResponse> routerFunction(Handler handler) {
    return route(POST("/api/v1/usuarios"), handler::listenPOSTUser)
        .and(route(GET("/api/v1/usuarios"), s -> handler.listenGETAllUsers()));
  }

  @Bean
  public RouterFunction<ServerResponse> health() {
    return route(GET("/health"), request -> ServerResponse.ok().bodyValue("OK"));
  }
}
