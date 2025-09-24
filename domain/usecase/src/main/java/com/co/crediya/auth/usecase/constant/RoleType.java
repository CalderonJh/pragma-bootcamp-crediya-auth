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

  public static RoleType fromString(String role) {
    for (RoleType roleType : RoleType.values()) {
      if (roleType.getValue().equalsIgnoreCase(role) || roleType.name().equalsIgnoreCase(role)) {
        return roleType;
      }
    }
    throw new IllegalArgumentException("No enum constant for value: " + role);
  }
}
