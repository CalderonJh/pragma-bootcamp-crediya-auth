package com.co.crediya.auth.r2dbc.repository.adapter;

import com.co.crediya.auth.model.user.Role;
import com.co.crediya.auth.r2dbc.entity.RoleEntity;
import com.co.crediya.auth.r2dbc.helper.ReactiveAdapterOperations;
import com.co.crediya.auth.r2dbc.repository.RoleRepository;
import com.co.crediya.auth.usecase.exception.DataNotFoundException;
import java.util.UUID;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class RoleRepositoryAdapter
    extends ReactiveAdapterOperations<Role, RoleEntity, String, RoleRepository>
    implements com.co.crediya.auth.model.user.gateways.RoleRepository {

  public RoleRepositoryAdapter(RoleRepository repository, ObjectMapper mapper) {
    super(repository, mapper, d -> mapper.map(d, Role.class));
  }

  @Override
  public Mono<Role> findDefaultRole() {
    return repository
        .findByName("USER")
        .switchIfEmpty(Mono.error(new DataNotFoundException("Default role not found")))
        .map(entity -> mapper.map(entity, Role.class));
  }

  @Override
  public Mono<Role> findUserRole(UUID userId) {
    return repository.findUserRole(userId).map(entity -> mapper.map(entity, Role.class));
  }
}
