package com.co.crediya.auth.r2dbc.repository;

import com.co.crediya.auth.r2dbc.entity.UserEntity;
import com.co.crediya.auth.r2dbc.projection.UserRow;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserRepository
    extends ReactiveCrudRepository<UserEntity, String>,
        ReactiveQueryByExampleExecutor<UserEntity> {

	Mono<Boolean> existsByEmail(String email);

  Mono<UserEntity> findByEmail(String email);

  @Query(
      """
	    SELECT
	        u.user_id AS user_id,
	        u.name AS name,
	        u.last_name AS last_name,
	        u.birth_date AS birth_date,
	        u.address AS address,
	        u.phone_number AS phone_number,
	        u.email AS email,
	        u.password AS password,
	        u.failed_login_attempts AS failed_login_attempts,
	        u.base_salary AS base_salary,
	        r.id AS role_id,
	        r.name AS role_name
	    FROM users u
	    JOIN roles r ON u.role_id = r.id
	    WHERE u.user_id = :id
	""")
  Mono<UserRow> findUserById(UUID id);

  @Query(
      """
	    SELECT
	        u.user_id AS user_id,
	        u.name AS name,
	        u.last_name AS last_name,
	        u.birth_date AS birth_date,
	        u.address AS address,
	        u.phone_number AS phone_number,
	        u.email AS email,
	        u.password AS password,
	        u.failed_login_attempts AS failed_login_attempts,
	        u.base_salary AS base_salary,
	        r.id AS role_id,
	        r.name AS role_name
	    FROM users u
	    JOIN roles r ON u.role_id = r.id
	""")
	Flux<UserRow> findAllRow();
}
