package com.plazoleta.plazoleta.infrastructure.adapter.input.rest;

import com.plazoleta.plazoleta.application.service.GestionarPlazoletaService;
import com.plazoleta.plazoleta.domain.model.Restaurante;
import com.plazoleta.plazoleta.infrastructure.adapter.input.rest.dto.CrearRestauranteRequest;
import com.plazoleta.plazoleta.infrastructure.adapter.input.rest.dto.RestaurantePageResponse;
import com.plazoleta.plazoleta.infrastructure.adapter.input.rest.dto.RestauranteResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/restaurantes")
public class RestauranteController {
	private final GestionarPlazoletaService service;
	public RestauranteController(GestionarPlazoletaService service) { this.service = service; }

	@PostMapping
	@Operation(summary = "Crear restaurante", security = @SecurityRequirement(name = "bearerAuth"))
	@ResponseStatus(HttpStatus.CREATED)
	public RestauranteResponse crear(@Valid @RequestBody CrearRestauranteRequest request,
			@RequestHeader("Authorization") String bearerToken) {
		Restaurante created = service.crearRestaurante(request.nombre(), request.nit(), request.direccion(),
				request.telefono(), request.urlLogo(), request.propietarioId(), bearerToken);
		return RestauranteResponse.from(created);
	}

	@GetMapping
	@Operation(summary = "Listar restaurantes", description = "Ordena por nombre ascendente y devuelve solo nombre y urlLogo por restaurante.",
			security = @SecurityRequirement(name = "bearerAuth"))
	public RestaurantePageResponse listar(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		return RestaurantePageResponse.from(service.listarRestaurantes(page, size));
	}
}
