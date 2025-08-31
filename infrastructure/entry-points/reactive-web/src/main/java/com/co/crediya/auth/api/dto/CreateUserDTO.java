package com.co.crediya.auth.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información para crear un nuevo usuario")
public class CreateUserDTO {

  @Schema(description = "Nombre del usuario", example = "Juan")
  private String name;

  @Schema(description = "Apellido del usuario", example = "Pérez")
  private String lastName;

  @Schema(description = "Fecha de nacimiento del usuario", example = "1990-01-01")
  private LocalDate birthDate;

  @Schema(description = "Dirección del usuario", example = "Calle Falsa 123")
  private String address;

  @Schema(description = "Número de teléfono del usuario", example = "+573001234567")
  private String phoneNumber;

  @Schema(description = "Correo electrónico del usuario", example = "juanperez@email.com")
  private String email;

  @Schema(description = "Salario base del usuario", example = "2500000.00")
  private BigDecimal baseSalary;

  @Schema(description = "Contraseña del usuario", example = "P@ssw0rd!")
  private String password;
}
