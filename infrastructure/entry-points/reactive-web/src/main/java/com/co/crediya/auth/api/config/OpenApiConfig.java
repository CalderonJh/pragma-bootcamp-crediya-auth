package com.co.crediya.auth.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("API Autorización")
                .description("API para la gestión de usuarios y roles")
                .version("1.0.0"));
  }

  @Bean
  public OpenApiCustomizer globalResponsesCustomizer() {
    return openApi ->
        openApi
            .getPaths()
            .values()
            .forEach(
                pathItem ->
                    pathItem
                        .readOperations()
                        .forEach(
                            operation -> {
                              operation
                                  .getResponses()
                                  .addApiResponse(
                                      "400",
                                      new io.swagger.v3.oas.models.responses.ApiResponse()
                                          .description("Invalid request"));
                              operation
                                  .getResponses()
                                  .addApiResponse(
                                      "404",
                                      new io.swagger.v3.oas.models.responses.ApiResponse()
                                          .description("Resource not found"));
                              operation
                                  .getResponses()
                                  .addApiResponse(
                                      "500",
                                      new io.swagger.v3.oas.models.responses.ApiResponse()
                                          .description("Internal server error"));
                            }));
  }
}
