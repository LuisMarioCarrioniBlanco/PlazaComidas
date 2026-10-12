package com.plazoleta.plazoleta.infrastructure.adapter.input.rest.dto;

import com.plazoleta.plazoleta.domain.model.Restaurante;

public record RestauranteResponse(String nombre, String urlLogo) {
	public static RestauranteResponse from(Restaurante restaurant) {
		return new RestauranteResponse(restaurant.nombre(), restaurant.urlLogo());
	}
}
