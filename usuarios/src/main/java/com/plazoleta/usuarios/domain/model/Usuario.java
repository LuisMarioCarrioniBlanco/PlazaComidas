package com.plazoleta.usuarios.domain.model;

import java.time.LocalDate;

public record Usuario(
		Long id,
		String nombre,
		String apellido,
		String documento,
		String celular,
		LocalDate fechaNacimiento,
		String correo,
		String claveHash,
		Rol rol,
		Long restauranteId) {
}
