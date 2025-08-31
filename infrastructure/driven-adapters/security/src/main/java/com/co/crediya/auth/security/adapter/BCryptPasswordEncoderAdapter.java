package com.co.crediya.auth.security.adapter;

import com.co.crediya.auth.model.user.gateways.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptPasswordEncoderAdapter implements PasswordEncoder {

  private final BCryptPasswordEncoder delegate = new BCryptPasswordEncoder();

  @Override
  public String encode(String rawPassword) {
    return delegate.encode(rawPassword);
  }

  @Override
  public boolean matches(String rawPassword, String encodedPassword) {
    return delegate.matches(rawPassword, encodedPassword);
  }
}
