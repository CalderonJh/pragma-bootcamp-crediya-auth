package com.co.crediya.auth.model.user;

import java.util.UUID;
import lombok.*;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Role {
  private UUID id;
  private String name;
}
