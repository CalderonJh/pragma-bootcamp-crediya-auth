package com.co.crediya.auth.model.user.gateways;

public interface PasswordEncoder {
  String encode(String rawPassword);

  boolean matches(String rawPassword, String encodedPassword);
}
