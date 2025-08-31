package com.co.crediya.auth.model.user.gateways;

import java.util.Map;

public interface TokenProvider {
  String generateToken(String subject, Map<String, Object> claims);
}
