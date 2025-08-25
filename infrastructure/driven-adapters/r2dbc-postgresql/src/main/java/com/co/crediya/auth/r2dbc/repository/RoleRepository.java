package com.co.crediya.auth.r2dbc.repository;

import com.co.crediya.auth.r2dbc.entity.RoleEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface RoleRepository
    extends ReactiveCrudRepository<RoleEntity, String>, ReactiveQueryByExampleExecutor<RoleEntity> {

  Mono<RoleEntity> findByName(String name);
}
