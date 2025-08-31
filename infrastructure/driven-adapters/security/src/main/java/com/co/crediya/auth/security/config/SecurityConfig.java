package com.co.crediya.auth.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

  @Value("${security.jwt.roles-claim:roles}")
  private String rolesClaim;

  @Bean
  public SecurityWebFilterChain springSecurityFilterChain(
      ServerHttpSecurity http, ReactiveJwtDecoder jwtDecoder) {
    // default simple mapping from claim -> GrantedAuthority
    JwtGrantedAuthoritiesConverter gaConverter = new JwtGrantedAuthoritiesConverter();
    gaConverter.setAuthorityPrefix("ROLE_");
    gaConverter.setAuthoritiesClaimName(rolesClaim); // e.g. "roles" or "scope"

    JwtAuthenticationConverter jwtAuthConverter = new JwtAuthenticationConverter();
    jwtAuthConverter.setJwtGrantedAuthoritiesConverter(gaConverter);

    // adapter converts sync JwtAuthenticationConverter -> reactive Converter<Jwt,
    // Mono<AbstractAuthenticationToken>>
    var reactiveConverter = new ReactiveJwtAuthenticationConverterAdapter(jwtAuthConverter);

    return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
        .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
        .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
        .authorizeExchange(
            ex ->
                ex.pathMatchers(
                        "/actuator/**",
                        "/health",
                        "/public/**",
                        "/webjars/swagger-ui/**",
                        "/v3/api-docs/**")
                    .permitAll()
                    .anyExchange()
                    .authenticated())
        .oauth2ResourceServer(
            oauth2 ->
                oauth2.jwt(
                    jwt ->
                        jwt.jwtDecoder(jwtDecoder).jwtAuthenticationConverter(reactiveConverter)))
        .build();
  }
}
