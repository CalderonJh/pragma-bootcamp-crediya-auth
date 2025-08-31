package com.co.crediya.auth.r2dbc.mapper;

import com.co.crediya.auth.model.user.Role;
import com.co.crediya.auth.model.user.User;
import com.co.crediya.auth.r2dbc.entity.UserEntity;

public class UserMapper {
  private UserMapper() {}

  public static UserEntity toEntity(User user) {
    return UserEntity.builder()
        .id(user.getId())
        .roleId(user.getRole().getId())
        .name(user.getName())
        .lastName(user.getLastName())
        .birthDate(user.getBirthDate())
        .address(user.getAddress())
        .phoneNumber(user.getPhoneNumber())
        .email(user.getEmail())
        .password(user.getPassword())
        .baseSalary(user.getBaseSalary())
        .build();
  }

  public static User toModel(UserEntity entity) {
    return User.builder()
        .id(entity.getId())
        .role(new Role(entity.getRoleId(), null))
        .name(entity.getName())
        .lastName(entity.getLastName())
        .birthDate(entity.getBirthDate())
        .address(entity.getAddress())
        .phoneNumber(entity.getPhoneNumber())
        .email(entity.getEmail())
        .baseSalary(entity.getBaseSalary())
        .build();
  }

  public static Role toRoleModel(Role role) {
    return Role.builder().id(role.getId()).name(role.getName()).build();
  }
}
