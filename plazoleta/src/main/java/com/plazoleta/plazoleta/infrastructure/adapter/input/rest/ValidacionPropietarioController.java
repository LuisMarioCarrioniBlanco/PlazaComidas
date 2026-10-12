package com.plazoleta.plazoleta.infrastructure.adapter.input.rest;

import com.plazoleta.plazoleta.application.service.GestionarPlazoletaService;
import com.plazoleta.plazoleta.domain.exception.AccesoDenegadoException;
import com.plazoleta.plazoleta.infrastructure.security.UsuarioPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/v1/internal/restaurantes")
public class ValidacionPropietarioController {
	private final GestionarPlazoletaService service;
	public ValidacionPropietarioController(GestionarPlazoletaService service) { this.service = service; }

	@GetMapping("/{restauranteId}/propietarios/{propietarioId}")
	@Operation(summary = "Verificar propiedad", security = @SecurityRequirement(name = "bearerAuth"))
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void verificar(@PathVariable Long restauranteId, @PathVariable Long propietarioId,
			@AuthenticationPrincipal UsuarioPrincipal principal) {
		if (!principal.id().equals(propietarioId)) {
			throw new AccesoDenegadoException("El propietario autenticado no coincide con el solicitado");
		}
		service.verificarPropiedadParaEmpleado(restauranteId, propietarioId);
	}
}
