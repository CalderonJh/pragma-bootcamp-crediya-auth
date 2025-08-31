package com.co.crediya.auth.security.adapter;

import com.co.crediya.auth.model.user.gateways.TokenProvider;
import java.time.Instant;
import java.util.Map;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProviderAdapter implements TokenProvider {

  private final JwtEncoder jwtEncoder;

  public JwtTokenProviderAdapter(JwtEncoder jwtEncoder) {
    this.jwtEncoder = jwtEncoder;
  }

  @Override
  public String generateToken(String subject, Map<String, Object> claims) {
    JwtClaimsSet claimsSet =
        JwtClaimsSet.builder()
            .subject(subject)
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(3600))
            .claims(c -> c.putAll(claims))
            .build();

    return this.jwtEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
  }
}
