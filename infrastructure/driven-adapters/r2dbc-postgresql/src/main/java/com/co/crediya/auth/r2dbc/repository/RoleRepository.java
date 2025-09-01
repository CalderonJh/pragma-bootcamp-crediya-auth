package com.co.crediya.auth.r2dbc.repository;

import com.co.crediya.auth.r2dbc.entity.RoleEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface RoleRepository
    extends ReactiveCrudRepository<RoleEntity, String>, ReactiveQueryByExampleExecutor<RoleEntity> {

  Mono<RoleEntity> findByName(String name);

  @Query(
      "select r.id as id, r.name as name from users u join roles r on u.role_id = r.id where u.user_id = :userId")
  Mono<RoleEntity> findUserRole(UUID userId);
}