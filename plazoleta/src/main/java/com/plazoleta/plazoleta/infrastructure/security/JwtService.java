package com.plazoleta.plazoleta.infrastructure.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
	private final javax.crypto.SecretKey key;
	public JwtService(@Value("${app.jwt.secret}") String secret) {
		if (secret.getBytes(StandardCharsets.UTF_8).length < 32) {
			throw new IllegalArgumentException("JWT_SECRET debe contener al menos 32 bytes");
		}
		this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}

	public UsuarioPrincipal validar(String token) throws JwtException {
		var claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
		return new UsuarioPrincipal(claims.get("uid", Long.class), claims.getSubject(),
				claims.get("rol", String.class), claims.get("restauranteId", Long.class));
	}
}
