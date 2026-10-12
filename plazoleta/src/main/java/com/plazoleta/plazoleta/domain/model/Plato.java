package com.plazoleta.plazoleta.domain.model;

public record Plato(Long id, String nombre, int precio, String descripcion, String urlImagen, String categoria,
		Long restauranteId, boolean activo) {
}
