package com.co.crediya.auth.usecase.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleType {
  ADMIN("ADMIN"),
  USER("USUARIO"),
  CONSULTANT("ASESOR");

  private final String value;

  public static boolean isAny(String value, RoleType... types) {
    for (RoleType type : types) if (type.getValue().equalsIgnoreCase(value)) return true;
    return false;
  }
}
