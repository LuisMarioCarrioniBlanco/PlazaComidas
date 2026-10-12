package com.plazoleta.usuarios.infrastructure.adapter.input.rest;

import com.plazoleta.usuarios.application.service.AutenticarUsuarioService;
import com.plazoleta.usuarios.infrastructure.adapter.input.rest.dto.LoginRequest;
import com.plazoleta.usuarios.infrastructure.adapter.input.rest.dto.LoginResponse;
import com.plazoleta.usuarios.infrastructure.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
	private final AutenticarUsuarioService autenticar;
	private final JwtService jwtService;

	public AuthController(AutenticarUsuarioService autenticar, JwtService jwtService) {
		this.autenticar = autenticar;
		this.jwtService = jwtService;
	}

	@PostMapping("/login")
	public LoginResponse login(@Valid @RequestBody LoginRequest request) {
		var user = autenticar.autenticar(request.correo(), request.clave());
		return new LoginResponse(jwtService.generar(user), "Bearer", jwtService.expiracionSegundos(), user.id(), user.rol());
	}
}
