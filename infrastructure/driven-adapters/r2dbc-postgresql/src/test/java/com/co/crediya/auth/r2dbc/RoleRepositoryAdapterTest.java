package com.co.crediya.auth.r2dbc;

import static org.mockito.Mockito.when;

import com.co.crediya.auth.model.user.Role;
import com.co.crediya.auth.r2dbc.entity.RoleEntity;
import com.co.crediya.auth.r2dbc.repository.RoleRepository;
import com.co.crediya.auth.r2dbc.repository.adapter.RoleRepositoryAdapter;
import com.co.crediya.auth.usecase.exception.DataNotFoundException;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class RoleRepositoryAdapterTest {

  @InjectMocks RoleRepositoryAdapter repositoryAdapter;
  @Mock RoleRepository repository;
  @Mock ObjectMapper mapper;

  @Test
  void findDefaultRoleTest() {
    UUID id = UUID.randomUUID();
    RoleEntity roleEntity = new RoleEntity(id, "USER");
    when(repository.findByName("USER")).thenReturn(Mono.just(roleEntity));
    when(mapper.map(roleEntity, Role.class)).thenReturn(new Role(id, "USER"));

    Mono<Role> result = repositoryAdapter.findDefaultRole();

    StepVerifier.create(result).expectNextMatches(r -> r.getName().equals("USER")).verifyComplete();
  }

  @Test
  void mustErrorWhenDefaultRoleNotFound() {
    when(repository.findByName("USER")).thenReturn(Mono.empty());

    Mono<Role> result = repositoryAdapter.findDefaultRole();

    StepVerifier.create(result)
        .expectErrorMatches(
            error ->
                error instanceof DataNotFoundException
                    && error.getMessage().equals("Default role not found"))
        .verify();
  }
}
