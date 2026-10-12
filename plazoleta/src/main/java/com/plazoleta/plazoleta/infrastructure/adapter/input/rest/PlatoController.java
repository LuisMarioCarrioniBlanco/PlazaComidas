package com.plazoleta.plazoleta.infrastructure.adapter.input.rest;

import com.plazoleta.plazoleta.application.service.GestionarPlazoletaService;
import com.plazoleta.plazoleta.infrastructure.adapter.input.rest.dto.CambiarEstadoPlatoRequest;
import com.plazoleta.plazoleta.infrastructure.adapter.input.rest.dto.CrearPlatoRequest;
import com.plazoleta.plazoleta.infrastructure.adapter.input.rest.dto.ModificarPlatoRequest;
import com.plazoleta.plazoleta.infrastructure.adapter.input.rest.dto.PlatoResponse;
import com.plazoleta.plazoleta.infrastructure.security.UsuarioPrincipal;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class PlatoController {
	private final GestionarPlazoletaService service;
	public PlatoController(GestionarPlazoletaService service) { this.service = service; }

	@PostMapping("/restaurantes/{restauranteId}/platos")
	@Operation(summary = "Crear plato", security = @SecurityRequirement(name = "bearerAuth"))
	@ResponseStatus(HttpStatus.CREATED)
	public PlatoResponse crear(@PathVariable Long restauranteId, @Valid @RequestBody CrearPlatoRequest request,
			@AuthenticationPrincipal UsuarioPrincipal propietario) {
		return PlatoResponse.from(service.crearPlato(restauranteId, propietario.id(), request.nombre(), request.precio(),
				request.descripcion(), request.urlImagen(), request.categoria()));
	}

	@PatchMapping("/platos/{platoId}")
	@Operation(summary = "Modificar precio y descripción del plato", security = @SecurityRequirement(name = "bearerAuth"))
	public PlatoResponse modificar(@PathVariable Long platoId, @Valid @RequestBody ModificarPlatoRequest request,
			@AuthenticationPrincipal UsuarioPrincipal propietario) {
		return PlatoResponse.from(service.modificarPlato(platoId, propietario.id(), request.precio(), request.descripcion()));
	}

	@PutMapping("/platos/{platoId}/estado")
	@Operation(summary = "Habilitar o deshabilitar plato", security = @SecurityRequirement(name = "bearerAuth"))
	public PlatoResponse estado(@PathVariable Long platoId, @Valid @RequestBody CambiarEstadoPlatoRequest request,
			@AuthenticationPrincipal UsuarioPrincipal propietario) {
		return PlatoResponse.from(service.cambiarEstado(platoId, propietario.id(), request.activo()));
	}
}
