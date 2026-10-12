package com.plazoleta.plazoleta.infrastructure.adapter.input.rest.dto;

import com.plazoleta.plazoleta.domain.model.ResultadoPaginado;
import java.util.List;

public record RestaurantePageResponse(List<RestauranteResponse> content, int page, int size,
		long totalElements, int totalPages) {
	public static RestaurantePageResponse from(ResultadoPaginado<com.plazoleta.plazoleta.domain.model.Restaurante> result) {
		return new RestaurantePageResponse(result.contenido().stream().map(RestauranteResponse::from).toList(),
				result.pagina(), result.tamano(), result.totalElementos(), result.totalPaginas());
	}
}
