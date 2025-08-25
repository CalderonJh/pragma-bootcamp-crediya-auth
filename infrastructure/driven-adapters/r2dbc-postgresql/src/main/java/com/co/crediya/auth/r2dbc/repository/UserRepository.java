package com.co.crediya.auth.r2dbc.repository;

import com.co.crediya.auth.r2dbc.entity.UserEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserRepository
    extends ReactiveCrudRepository<UserEntity, String>,
        ReactiveQueryByExampleExecutor<UserEntity> {

	Mono<Boolean> existsByEmail(String email);
}
