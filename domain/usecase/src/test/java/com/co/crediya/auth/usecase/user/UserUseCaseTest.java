package com.co.crediya.auth.usecase.user;

import static com.co.crediya.auth.usecase.util.validation.ReactiveValidators.MessageTemplate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.co.crediya.auth.model.user.Role;
import com.co.crediya.auth.model.user.User;
import com.co.crediya.auth.model.user.gateways.RoleRepository;
import com.co.crediya.auth.model.user.gateways.UserRepository;
import com.co.crediya.auth.usecase.exception.BusinessRuleException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class UserUseCaseTest {
  private UserRepository userRepository;
  private RoleRepository roleRepository;
  private UserUseCase useCase;
  private User user;

  @BeforeEach
  void setUp() {
    roleRepository = mock(RoleRepository.class);
    userRepository = mock(UserRepository.class);
    useCase = new UserUseCase(userRepository, roleRepository);
    user =
        User.builder()
            .name("name")
            .lastName("lastName")
            .birthDate(LocalDate.of(1990, 1, 1))
            .address("address")
            .phoneNumber("1234567890")
            .email("email@email.com")
            .baseSalary(BigDecimal.valueOf(1000000))
            .build();
  }

  @Test
  @DisplayName("Should pass validation with valid user")
  void shouldPassValidationWithValidUser() {
    Role defaultRole = new Role(UUID.randomUUID(), "USER");

    when(userRepository.existsByEmail("email@email.com")).thenReturn(Mono.just(false));
    when(roleRepository.findDefaultRole()).thenReturn(Mono.just(defaultRole));
    when(userRepository.saveUser(any(User.class))).thenReturn(Mono.just(user));
    when(userRepository.saveUser(any(User.class))).thenReturn(Mono.just(user));
    StepVerifier.create(useCase.saveUser(user))
        .assertNext(
            u -> {
              assertThat(u.getName()).isEqualTo("name");
              assertThat(u.getLastName()).isEqualTo("lastName");
              assertThat(u.getBirthDate()).isEqualTo(LocalDate.of(1990, 1, 1));
              assertThat(u.getAddress()).isEqualTo("address");
              assertThat(u.getPhoneNumber()).isEqualTo("1234567890");
              assertThat(u.getEmail()).isEqualTo("email@email.com");
              assertThat(u.getBaseSalary()).isEqualByComparingTo(BigDecimal.valueOf(1000000.00));
              assertThat(u.getRole()).isEqualTo(defaultRole);
            })
        .verifyComplete();
  }

  @Test
  @DisplayName("Should fail when name is empty")
  void shouldFailWhenNameIsEmpty() {
    user.setName("");
    StepVerifier.create(useCase.saveUser(user))
        .expectErrorSatisfies(
            e ->
                assertThat(e)
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessage(MessageTemplate.NOT_EMPTY.render("Name")))
        .verify();
  }

  @Test
  @DisplayName("Should fail when last name is null")
  void shouldFailWhenLastNameIsNull() {
    user.setLastName(null);
    StepVerifier.create(useCase.saveUser(user))
        .expectErrorSatisfies(
            e ->
                assertThat(e)
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessage(MessageTemplate.NOT_EMPTY.render("Last name")))
        .verify();
  }

  @Test
  @DisplayName("Should fail when birth date is null")
  void shouldFailWhenBirthDateIsNull() {
    user.setBirthDate(null);
    StepVerifier.create(useCase.saveUser(user))
        .expectErrorSatisfies(
            e ->
                assertThat(e)
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessage(MessageTemplate.NOT_NULL.render("Birth date")))
        .verify();
  }

  @Test
  @DisplayName("Should fail when birth date is in the future")
  void shouldFailWhenBirthDateIsFuture() {
    user.setBirthDate(LocalDate.now().plusDays(1));
    StepVerifier.create(useCase.saveUser(user))
        .expectErrorSatisfies(
            e ->
                assertThat(e)
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessage(MessageTemplate.PAST_DATE.render("Birth date")))
        .verify();
  }

  @Test
  @DisplayName("Should fail when address is empty")
  void shouldFailWhenAddressIsEmpty() {
    user.setAddress(" ");
    StepVerifier.create(useCase.saveUser(user))
        .expectErrorSatisfies(
            e ->
                assertThat(e)
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessage(MessageTemplate.NOT_EMPTY.render("Address")))
        .verify();
  }

  @Test
  @DisplayName("Should fail when phone number is empty")
  void shouldFailWhenPhoneNumberIsEmpty() {
    user.setPhoneNumber("");
    StepVerifier.create(useCase.saveUser(user))
        .expectErrorSatisfies(
            e ->
                assertThat(e)
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessage(MessageTemplate.NOT_EMPTY.render("Phone number")))
        .verify();
  }

  @Test
  @DisplayName("Should fail when email is invalid")
  void shouldFailWhenEmailIsInvalid() {
    user.setEmail("wrong-email");
    StepVerifier.create(useCase.saveUser(user))
        .expectErrorSatisfies(
            e ->
                assertThat(e)
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessage(MessageTemplate.EMAIL.render()))
        .verify();
  }

  @Test
  @DisplayName("Should fail when base salary is null")
  void shouldFailWhenBaseSalaryIsNull() {
    user.setBaseSalary(null);
    StepVerifier.create(useCase.saveUser(user))
        .expectErrorSatisfies(
            e ->
                assertThat(e)
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessage(MessageTemplate.NOT_NULL.render("Base salary")))
        .verify();
  }

  @Test
  @DisplayName("Should fail when base salary is below minimum")
  void shouldFailWhenBaseSalaryIsBelowMinimum() {
    user.setBaseSalary(BigDecimal.valueOf(-100));
    StepVerifier.create(useCase.saveUser(user))
        .expectErrorSatisfies(
            e ->
                assertThat(e)
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessage(
                        MessageTemplate.RANGE.render(
                            "Base salary", BigDecimal.ZERO, BigDecimal.valueOf(15000000.00))))
        .verify();
  }

  @Test
  @DisplayName("Should fail when base salary is above maximum")
  void shouldFailWhenBaseSalaryIsAboveMaximum() {
    user.setBaseSalary(BigDecimal.valueOf(20_000_000));
    StepVerifier.create(useCase.saveUser(user))
        .expectErrorSatisfies(
            e ->
                assertThat(e)
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessage(
                        MessageTemplate.RANGE.render(
                            "Base salary", BigDecimal.ZERO, BigDecimal.valueOf(15000000.00))))
        .verify();
  }

  @Test
  @DisplayName("Error when email already exists")
  void shouldErrorWhenEmailAlreadyExists() {
    when(userRepository.existsByEmail(user.getEmail())).thenReturn(Mono.just(true));
    StepVerifier.create(useCase.saveUser(user))
        .expectErrorSatisfies(
            e ->
                assertThat(e)
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessage("Email already exists"))
        .verify();
  }

  @Test
  @DisplayName("Get all users test")
  void getAllUsersTest() {
    when(userRepository.findAllUsers()).thenReturn(Mono.just(user).flux());

    StepVerifier.create(useCase.getUsers()).expectNextCount(1).verifyComplete();
  }
}
