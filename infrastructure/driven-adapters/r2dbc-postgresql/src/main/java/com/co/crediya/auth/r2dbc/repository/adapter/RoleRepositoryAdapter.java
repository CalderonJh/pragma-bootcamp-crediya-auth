package com.co.crediya.auth.r2dbc.repository.adapter;

import com.co.crediya.auth.model.exception.DataNotFoundException;
import com.co.crediya.auth.model.role.Role;
import com.co.crediya.auth.r2dbc.entity.RoleEntity;
import com.co.crediya.auth.r2dbc.helper.ReactiveAdapterOperations;
import com.co.crediya.auth.r2dbc.repository.RoleRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Repository
public class RoleRepositoryAdapter
    extends ReactiveAdapterOperations<Role, RoleEntity, String, RoleRepository>
    implements com.co.crediya.auth.model.role.gateways.RoleRepository {
  private final TransactionalOperator transactionalOperator;

  public RoleRepositoryAdapter(
      RoleRepository repository, ObjectMapper mapper, TransactionalOperator transactionalOperator) {
    super(repository, mapper, d -> mapper.map(d, Role.class));
    this.transactionalOperator = transactionalOperator;
  }

  @Override
  public Mono<Role> findDefaultRole() {
    return repository
        .findByName("USER")
        .switchIfEmpty(Mono.error(new DataNotFoundException("Default role not found")))
        .map(entity -> mapper.map(entity, Role.class));
  }
}
