package com.plazoleta.usuarios.application.service;

import com.plazoleta.usuarios.application.port.out.PasswordHasherPort;
import com.plazoleta.usuarios.application.port.out.UsuarioRepositoryPort;
import com.plazoleta.usuarios.domain.exception.CredencialesInvalidasException;
import com.plazoleta.usuarios.domain.model.Usuario;
import java.util.Locale;

public class AutenticarUsuarioService {
	private final UsuarioRepositoryPort usuarios;
	private final PasswordHasherPort passwordHasher;

	public AutenticarUsuarioService(UsuarioRepositoryPort usuarios, PasswordHasherPort passwordHasher) {
		this.usuarios = usuarios;
		this.passwordHasher = passwordHasher;
	}

	public Usuario autenticar(String correo, String clave) {
		return usuarios.buscarPorCorreo(correo.trim().toLowerCase(Locale.ROOT))
				.filter(usuario -> passwordHasher.matches(clave, usuario.claveHash()))
				.orElseThrow(CredencialesInvalidasException::new);
	}
}
