package com.co.crediya.auth.model.role.gateways;

import com.co.crediya.auth.model.role.Role;
import reactor.core.publisher.Mono;

public interface RoleRepository {
  Mono<Role> findDefaultRole();
}
