package com.co.crediya.auth.r2dbc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.co.crediya.auth.model.user.Role;
import com.co.crediya.auth.model.user.User;
import com.co.crediya.auth.r2dbc.entity.RoleEntity;
import com.co.crediya.auth.r2dbc.entity.UserEntity;
import com.co.crediya.auth.r2dbc.mapper.UserMapper;
import com.co.crediya.auth.r2dbc.projection.UserRow;
import com.co.crediya.auth.r2dbc.repository.RoleRepository;
import com.co.crediya.auth.r2dbc.repository.UserRepository;
import com.co.crediya.auth.r2dbc.repository.adapter.UserRepositoryAdapter;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class UserRepositoryAdapterTest {

  @InjectMocks UserRepositoryAdapter repositoryAdapter;
  @Mock UserRepository repository;
  @Mock RoleRepository roleRepository;
  @Mock TransactionalOperator transactionalOperator;
  @Mock ObjectMapper mapper;

  @Test
  @SuppressWarnings("unchecked")
  void mustSaveUser() {
    UUID roleId = UUID.randomUUID();
    Role role = new Role(roleId, "USER");
    RoleEntity roleEntity = new RoleEntity(roleId, "USER");

    User user = User.builder().role(role).build();
    UserEntity entity = UserMapper.toEntity(user);

    when(repository.save(any(UserEntity.class))).thenReturn(Mono.just(entity));
    when(transactionalOperator.transactional(any(Mono.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(roleRepository.findById(String.valueOf(roleId))).thenReturn(Mono.just(roleEntity));
    when(mapper.map(roleEntity, Role.class)).thenReturn(role);

    Mono<User> result = repositoryAdapter.saveUser(user);

    StepVerifier.create(result)
        .expectNextMatches(
            saved -> saved.getRole() != null && saved.getRole().getName().equals("USER"))
        .verifyComplete();
  }

  @Test
	@DisplayName("Must return true when email exists")
  void mustReturnTrueWhenEmailExists() {
    String email = "test@example.com";

    when(repository.existsByEmail(email)).thenReturn(Mono.just(true));

    Mono<Boolean> result = repositoryAdapter.existsByEmail(email);

    StepVerifier.create(result).expectNext(true).verifyComplete();
  }

  @Test
	@DisplayName("Must return false when email does not exist")
  void mustReturnFalseWhenEmailDoesNotExist() {
    String email = "notfound@example.com";

    when(repository.existsByEmail(email)).thenReturn(Mono.just(false));

    Mono<Boolean> result = repositoryAdapter.existsByEmail(email);

    StepVerifier.create(result).expectNext(false).verifyComplete();
  }

	@Test
	@DisplayName("Get all users test")
  void getAllUsersTest() {
    when(repository.findAllRow()).thenReturn(Flux.just(new UserRow()));
    Flux<User> result = repositoryAdapter.findAllUsers();
    StepVerifier.create(result).expectNextCount(1).verifyComplete();
  }
}
