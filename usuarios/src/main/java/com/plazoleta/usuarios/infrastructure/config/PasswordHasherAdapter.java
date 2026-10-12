package com.plazoleta.usuarios.infrastructure.config;

import com.plazoleta.usuarios.application.port.out.PasswordHasherPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordHasherAdapter implements PasswordHasherPort {
	private final PasswordEncoder encoder;

	public PasswordHasherAdapter(PasswordEncoder encoder) {
		this.encoder = encoder;
	}

	@Override
	public String hash(String rawPassword) {
		return encoder.encode(rawPassword);
	}

	@Override
	public boolean matches(String rawPassword, String encodedPassword) {
		return encoder.matches(rawPassword, encodedPassword);
	}
}
