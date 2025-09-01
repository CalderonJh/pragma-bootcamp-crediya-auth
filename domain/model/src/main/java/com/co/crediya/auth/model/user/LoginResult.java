package com.co.crediya.auth.model.user;

import lombok.*;
import lombok.AllArgsConstructor;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResult {
  private boolean success;
  private int failedAttempts;
  private int maxAttempts;
  private String token;
}
