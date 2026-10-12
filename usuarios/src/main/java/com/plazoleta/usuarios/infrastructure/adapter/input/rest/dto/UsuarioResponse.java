package com.plazoleta.usuarios.infrastructure.adapter.input.rest.dto;

import com.plazoleta.usuarios.domain.model.Rol;

public record UsuarioResponse(Long id, String nombre, String apellido, String documento, String celular,
		String correo, Rol rol) {
	public static UsuarioResponse from(com.plazoleta.usuarios.domain.model.Usuario user) {
		return new UsuarioResponse(user.id(), user.nombre(), user.apellido(), user.documento(), user.celular(),
				user.correo(), user.rol());
	}
}
