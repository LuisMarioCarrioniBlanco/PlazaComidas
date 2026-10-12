package com.plazoleta.usuarios.application.service;

import com.plazoleta.usuarios.application.port.out.UsuarioRepositoryPort;
import com.plazoleta.usuarios.domain.exception.UsuarioNoEncontradoException;
import com.plazoleta.usuarios.domain.model.Usuario;

public class ConsultarUsuarioService {
	private final UsuarioRepositoryPort usuarios;
	public ConsultarUsuarioService(UsuarioRepositoryPort usuarios) { this.usuarios = usuarios; }
	public Usuario consultarPorId(Long id) {
		return usuarios.buscarPorId(id).orElseThrow(UsuarioNoEncontradoException::new);
	}
}
