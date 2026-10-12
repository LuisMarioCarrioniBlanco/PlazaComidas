package com.plazoleta.plazoleta.infrastructure.adapter.input.rest.dto;

import com.plazoleta.plazoleta.domain.model.Plato;

public record PlatoResponse(Long id, String nombre, int precio, String descripcion, String urlImagen, String categoria,
		Long restauranteId, boolean activo) {
	public static PlatoResponse from(Plato plato) {
		return new PlatoResponse(plato.id(), plato.nombre(), plato.precio(), plato.descripcion(), plato.urlImagen(),
				plato.categoria(), plato.restauranteId(), plato.activo());
	}
}
