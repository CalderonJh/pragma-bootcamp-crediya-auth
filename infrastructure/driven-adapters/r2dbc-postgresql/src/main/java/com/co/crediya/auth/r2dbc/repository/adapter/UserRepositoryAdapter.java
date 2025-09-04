package com.co.crediya.auth.r2dbc.repository.adapter;

import com.co.crediya.auth.model.user.Role;
import com.co.crediya.auth.model.user.User;
import com.co.crediya.auth.r2dbc.entity.UserEntity;
import com.co.crediya.auth.r2dbc.helper.ReactiveAdapterOperations;
import com.co.crediya.auth.r2dbc.mapper.UserMapper;
import com.co.crediya.auth.r2dbc.repository.RoleRepository;
import com.co.crediya.auth.r2dbc.repository.UserRepository;

import java.util.Collection;
import java.util.UUID;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class UserRepositoryAdapter
    extends ReactiveAdapterOperations<User, UserEntity, String, UserRepository>
    implements com.co.crediya.auth.model.user.gateways.UserRepository {
  private final TransactionalOperator transactionalOperator;
  private final RoleRepository roleRepository;

  public UserRepositoryAdapter(
      UserRepository repository,
      ObjectMapper mapper,
      TransactionalOperator transactionalOperator,
      RoleRepository roleRepository) {
    super(repository, mapper, d -> mapper.map(d, User.class));
    this.transactionalOperator = transactionalOperator;
    this.roleRepository = roleRepository;
  }

  @Override
  public Mono<User> saveUser(User user) {
    return repository
        .save(UserMapper.toEntity(user))
        .as(transactionalOperator::transactional)
        .flatMap(
            ent ->
                roleRepository
                    .findById(String.valueOf(ent.getRoleId()))
                    .map(
                        roleEntity -> {
                          User saved = UserMapper.toModel(ent);
                          saved.setRole(mapper.map(roleEntity, Role.class));
                          return saved;
                        }));
  }

  @Override
  public Flux<User> findAllUsers() {
    return repository.findAllRow().map(UserMapper::toModel);
  }

  @Override
  public Mono<Boolean> existsByEmail(String email) {
    return repository.existsByEmail(email);
  }

	@Override
	public Mono<User> findByEmail(String email) {
    return repository.findByEmail(email).map(UserMapper::toModel);
	}

  @Override
  public Mono<User> findById(UUID id) {
    return repository.findUserById(id).map(UserMapper::toModel);
  }

	@Override
	public Flux<User> findByIdIn(Collection<UUID> ids) {
    return repository.findByIdIn(ids).map(UserMapper::toModel);
	}
}
