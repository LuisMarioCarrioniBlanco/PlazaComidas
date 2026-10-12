package com.plazoleta.usuarios.infrastructure.security;

import com.plazoleta.usuarios.domain.model.Rol;
import com.plazoleta.usuarios.domain.model.Usuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
	private final javax.crypto.SecretKey key;
	private final long expirationSeconds;

	public JwtService(@Value("${app.jwt.secret}") String secret,
			@Value("${app.jwt.expiration-seconds:3600}") long expirationSeconds) {
		if (secret.getBytes(StandardCharsets.UTF_8).length < 32) {
			throw new IllegalArgumentException("JWT_SECRET debe contener al menos 32 bytes");
		}
		this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		this.expirationSeconds = expirationSeconds;
	}

	public String generar(Usuario usuario) {
		Instant now = Instant.now();
		return Jwts.builder().subject(usuario.correo()).claim("uid", usuario.id())
				.claim("rol", usuario.rol().name()).claim("restauranteId", usuario.restauranteId())
				.issuedAt(Date.from(now)).expiration(Date.from(now.plusSeconds(expirationSeconds)))
				.signWith(key).compact();
	}

	public UsuarioPrincipal validar(String token) throws JwtException {
		var claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
		return new UsuarioPrincipal(claims.get("uid", Long.class), claims.getSubject(),
				Rol.valueOf(claims.get("rol", String.class)), claims.get("restauranteId", Long.class));
	}

	public long expiracionSegundos() {
		return expirationSeconds;
	}
}
