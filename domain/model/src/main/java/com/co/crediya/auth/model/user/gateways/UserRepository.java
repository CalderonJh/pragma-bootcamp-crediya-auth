package com.co.crediya.auth.model.user.gateways;

import com.co.crediya.auth.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository {
	Mono<User> saveUser(User user);

	Flux<User> findAllUsers();

	Mono<Boolean> existsByEmail(String email);
}
