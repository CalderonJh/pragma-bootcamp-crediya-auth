package com.co.crediya.auth.model.user.gateways;

import com.co.crediya.auth.model.user.Role;
import reactor.core.publisher.Mono;

public interface RoleRepository {
  Mono<Role> findDefaultRole();
}
