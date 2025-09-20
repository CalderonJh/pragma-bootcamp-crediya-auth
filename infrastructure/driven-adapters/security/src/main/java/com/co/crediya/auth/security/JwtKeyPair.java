package com.co.crediya.auth.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtKeyPair {
	private String publicKey;
	private String privateKey;
}
