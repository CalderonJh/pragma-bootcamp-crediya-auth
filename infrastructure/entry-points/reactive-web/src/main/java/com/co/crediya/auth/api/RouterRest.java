package com.co.crediya.auth.api;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import com.co.crediya.auth.api.config.path.UserPath;
import com.co.crediya.auth.model.user.User;
import io.swagger.v3.oas.annotations.Operation;
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
  private final UserPath userPath;

  @RouterOperations({
    @RouterOperation(
        path = "/api/v1/users",
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
                  @ApiResponse(responseCode = "400", description = "Invalid request")
                }))
  })
  @Bean
  public RouterFunction<ServerResponse> routerFunction(Handler handler) {
    return route(POST(userPath.getSave()), handler::listenPOSTUser);
    //        .andRoute(POST("/api/usecase/otherpath"), handler::listenPOSTUseCase)
    //        .and(route(GET("/api/otherusercase/path"), handler::listenGETOtherUseCase));
  }
}
