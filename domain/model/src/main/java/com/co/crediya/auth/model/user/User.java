package com.co.crediya.auth.model.user;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
  private UUID id;
  private String name;
  private String lastName;
  private LocalDate birthDate;
  private String address;
  private String phoneNumber;
  private String email;
  private BigDecimal baseSalary;
  private String password;
  private Role role;
}
