package com.co.crediya.auth.api.mapper;

import com.co.crediya.auth.api.dto.CreateUserDTO;
import com.co.crediya.auth.api.dto.UserResponseDTO;
import com.co.crediya.auth.model.user.User;

public class UserMapper {
  private UserMapper() {}

  public static User toModel(CreateUserDTO dto) {
    return User.builder()
        .name(dto.getName())
        .lastName(dto.getLastName())
        .birthDate(dto.getBirthDate())
        .address(dto.getAddress())
        .phoneNumber(dto.getPhoneNumber())
        .email(dto.getEmail())
        .baseSalary(dto.getBaseSalary())
        .password(dto.getPassword())
        .build();
  }

  public static UserResponseDTO toResponse(User user) {
    return UserResponseDTO.builder()
        .id(user.getId())
        .name(user.getName())
        .lastName(user.getLastName())
        .birthDate(user.getBirthDate())
        .address(user.getAddress())
        .phoneNumber(user.getPhoneNumber())
        .email(user.getEmail())
        .baseSalary(user.getBaseSalary())
        .role(user.getRole().getName())
        .build();
  }
}
