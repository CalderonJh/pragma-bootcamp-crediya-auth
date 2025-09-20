package com.co.crediya.auth.security.config;

import static java.util.Objects.requireNonNull;

import co.com.bancolombia.secretsmanager.api.GenericManagerAsync;
import co.com.bancolombia.secretsmanager.api.exceptions.SecretException;
import com.co.crediya.auth.security.JwtKeyPair;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.InternalException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;

@Configuration
@RequiredArgsConstructor
public class JwtConfig {
  private final GenericManagerAsync secretManager;

  @Value("${aws.secrets.jwt-key-pair}")
  private String secretName;

  @Bean
  public RSAPrivateKey rsaPrivateKey() {
    try {
      JwtKeyPair pair = secretManager.getSecret(secretName, JwtKeyPair.class).block();
      return parsePrivateKey(requireNonNull(pair).getPrivateKey());
    } catch (SecretException e) {
      throw new InternalError(e);
    }
  }

  @Bean
  public RSAPublicKey rsaPublicKey() {
    try {
      JwtKeyPair pair = secretManager.getSecret(secretName, JwtKeyPair.class).block();
      return parsePublicKey(requireNonNull(pair).getPublicKey());
    } catch (SecretException e) {
      throw new InternalError(e);
    }
  }

  private RSAPrivateKey parsePrivateKey(String pem) {
    if (pem == null) {
      throw new IllegalArgumentException("Private key is null");
    }

    try {
      String sanitized =
          pem.replace("-----BEGIN PRIVATE KEY-----", "")
              .replace("-----END PRIVATE KEY-----", "")
              .replaceAll("\\s", "");
      return (RSAPrivateKey)
          KeyFactory.getInstance("RSA")
              .generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(sanitized)));
    } catch (Exception e) {
      throw new InternalException("Error parsing private key", e);
    }
  }

  private RSAPublicKey parsePublicKey(String pem) {
    if (pem == null) throw new IllegalArgumentException("Public key is null");
    try {
      String sanitized =
          pem.replace("-----BEGIN PUBLIC KEY-----", "")
              .replace("-----END PUBLIC KEY-----", "")
              .replaceAll("\\s", "");
      return (RSAPublicKey)
          KeyFactory.getInstance("RSA")
              .generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(sanitized)));
    } catch (Exception e) {
      throw new InternalException("Error parsing public key", e);
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
