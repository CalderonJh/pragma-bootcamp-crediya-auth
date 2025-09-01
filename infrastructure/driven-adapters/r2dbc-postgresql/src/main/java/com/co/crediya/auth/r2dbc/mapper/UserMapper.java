package com.co.crediya.auth.r2dbc.mapper;

import com.co.crediya.auth.model.user.Role;
import com.co.crediya.auth.model.user.User;
import com.co.crediya.auth.r2dbc.entity.UserEntity;
import com.co.crediya.auth.r2dbc.projection.UserRow;

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
        .failedLoginAttempts(user.getFailedLoginAttempts())
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
        .password(entity.getPassword())
        .failedLoginAttempts(entity.getFailedLoginAttempts())
        .build();
  }

  public static User toModel(UserRow row) {
    return User.builder()
        .id(row.getId())
        .role(new Role(row.getRoleId(), null))
        .name(row.getName())
        .lastName(row.getLastName())
        .birthDate(row.getBirthDate())
        .address(row.getAddress())
        .phoneNumber(row.getPhoneNumber())
        .email(row.getEmail())
        .baseSalary(row.getBaseSalary())
        .password(row.getPassword())
        .failedLoginAttempts(row.getFailedLoginAttempts())
        .role(new Role(row.getRoleId(), row.getRoleName()))
        .build();
  }

  public static Role toRoleModel(Role role) {
    return Role.builder().id(role.getId()).name(role.getName()).build();
  }
}
