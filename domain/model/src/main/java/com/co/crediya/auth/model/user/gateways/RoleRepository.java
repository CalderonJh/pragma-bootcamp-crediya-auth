package com.co.crediya.auth.model.user.gateways;

import com.co.crediya.auth.model.user.Role;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface RoleRepository {
  Mono<Role> findDefaultRole();

  Mono<Role> findUserRole(UUID userId);
}
