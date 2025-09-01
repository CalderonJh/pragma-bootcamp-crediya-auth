package com.co.crediya.auth.usecase.util.validation;

import com.co.crediya.auth.model.user.User;
import com.co.crediya.auth.usecase.constant.RoleType;
import com.co.crediya.auth.usecase.exception.PermissionException;
import reactor.core.publisher.Mono;

public class RoleValidator {

	public static final String PERMISSION_DENIED_MESSAGE = "No permission to perform this action";

	private RoleValidator() {}

  public static Mono<Void> hasAnyRole(User actor, RoleType... roles) {
    for (RoleType type : roles)
      if (type.getValue().equalsIgnoreCase(actor.getRole().getName())) return Mono.empty();
    return Mono.error(new PermissionException(PERMISSION_DENIED_MESSAGE));
  }
  public static Mono<Void> isAnyRole(String role, RoleType... roles) {
    for (RoleType type : roles)
      if (type.getValue().equalsIgnoreCase(role)) return Mono.empty();
    return Mono.error(new PermissionException(PERMISSION_DENIED_MESSAGE));
  }
}
