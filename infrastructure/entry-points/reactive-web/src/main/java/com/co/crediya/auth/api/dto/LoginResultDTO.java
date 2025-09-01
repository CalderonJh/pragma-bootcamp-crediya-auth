package com.co.crediya.auth.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resultado del intento de inicio de sesión")
public class LoginResultDTO {
  private boolean success;
  private int failedAttempts;
  private int maxAttempts;
}
