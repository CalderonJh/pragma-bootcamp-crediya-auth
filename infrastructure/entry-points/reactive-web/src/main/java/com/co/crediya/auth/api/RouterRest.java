package com.co.crediya.auth.api;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import com.co.crediya.auth.api.handler.UserHandler;
import com.co.crediya.auth.api.routes.UserRoutes;
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
        path = UserRoutes.SIGN_UP_URL,
        method = RequestMethod.POST,
        beanClass = UserHandler.class,
        beanMethod = "listenPOSTUser"),
    @RouterOperation(
        path = UserRoutes.SIGN_IN_URL,
        method = RequestMethod.POST,
        beanClass = UserHandler.class,
        beanMethod = "listenPOSTLoginUser"),
    @RouterOperation(
        path = UserRoutes.GET_BY_ID,
        method = RequestMethod.GET,
        beanClass = UserHandler.class,
        beanMethod = "listenGETUserById"),
    @RouterOperation(
        path = UserRoutes.GET_BY_ID_IN,
        method = RequestMethod.GET,
        beanClass = UserHandler.class,
        beanMethod = "listenGETUserByIdIn"),
    @RouterOperation(
        path = UserRoutes.GET_BY_ROLE,
        method = RequestMethod.GET,
        beanClass = UserHandler.class,
        beanMethod = "listenGETUserByRole"),
    @RouterOperation(
        path = UserRoutes.BASE_URL,
        method = RequestMethod.GET,
        beanClass = UserHandler.class,
        beanMethod = "listenGETAllUsers")
  })
  @Bean
  public RouterFunction<ServerResponse> userRouting(UserHandler handler) {
    return route(POST(UserRoutes.SIGN_UP_URL), handler::listenPOSTUser)
        .andRoute(POST(UserRoutes.SIGN_IN_URL), handler::listenPOSTLoginUser)
        .andRoute(GET(UserRoutes.GET_BY_ID), handler::listenGETUserById)
        .andRoute(POST(UserRoutes.GET_BY_ID_IN), handler::listenGETUserByIdIn)
        .andRoute(GET(UserRoutes.GET_BY_ROLE), handler::listenGETUserByRole)
        .and(route(GET(UserRoutes.BASE_URL), s -> handler.listenGETAllUsers()));
  }

  @Bean
  public RouterFunction<ServerResponse> health() {
    return route(GET("/health"), request -> ServerResponse.ok().bodyValue("OK"));
  }
}
