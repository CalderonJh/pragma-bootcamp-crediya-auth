package com.co.crediya.auth.r2dbc.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class UserEntity {
  @Id
  @Column("user_id")
  private UUID id;

  private UUID roleId;
  private String name;
  private String lastName;
  private LocalDate birthDate;
  private String address;
  private String phoneNumber;
  private String email;
  private String password;
  private BigDecimal baseSalary;
}
