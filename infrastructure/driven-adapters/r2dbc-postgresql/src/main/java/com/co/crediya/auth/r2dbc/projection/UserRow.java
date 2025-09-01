package com.co.crediya.auth.r2dbc.projection;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.*;
import org.springframework.data.relational.core.mapping.Column;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRow {
  @Column("user_id")
  private UUID id;

  @Column("name")
  private String name;

  @Column("last_name")
  private String lastName;

  @Column("birth_date")
  private LocalDate birthDate;

  @Column("address")
  private String address;

  @Column("phone_number")
  private String phoneNumber;

  @Column("email")
  private String email;

  @Column("password")
  private String password;

  @Column("failed_login_attempts")
  private Integer failedLoginAttempts;

  @Column("base_salary")
  private BigDecimal baseSalary;

  @Column("role_id")
  private UUID roleId;

  @Column("role_name")
  private String roleName;
}
