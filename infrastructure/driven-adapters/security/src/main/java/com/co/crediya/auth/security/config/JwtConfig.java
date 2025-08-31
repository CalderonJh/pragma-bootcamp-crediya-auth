package com.co.crediya.auth.security.config;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import java.io.IOException;
import java.io.InputStream;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.security.converter.RsaKeyConverters;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;

@Configuration
public class JwtConfig {

  @Bean
  public RSAPrivateKey rsaPrivateKey(@Value("${security.jwt.private-key-path}") Resource privateKey)
      throws IOException {
    try (InputStream inputStream = privateKey.getInputStream()) {
      return RsaKeyConverters.pkcs8().convert(inputStream);
    }
  }

  @Bean
  public RSAPublicKey rsaPublicKey(@Value("${security.jwt.public-key-path}") Resource publicKey)
      throws IOException {
    try (InputStream inputStream = publicKey.getInputStream()) {
      return RsaKeyConverters.x509().convert(inputStream);
    }
  }

  @Bean
  public JwtEncoder jwtEncoder(RSAPrivateKey privateKey, RSAPublicKey publicKey) {
    JWK jwk =
        new RSAKey.Builder(publicKey)
            .privateKey(privateKey)
            .algorithm(JWSAlgorithm.RS256)
            .keyUse(KeyUse.SIGNATURE)
            .build();
    JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
    return new NimbusJwtEncoder(jwks);
  }

  @Bean
  public ReactiveJwtDecoder jwtDecoder(RSAPublicKey publicKey) {
    return NimbusReactiveJwtDecoder.withPublicKey(publicKey).build();
  }
}
