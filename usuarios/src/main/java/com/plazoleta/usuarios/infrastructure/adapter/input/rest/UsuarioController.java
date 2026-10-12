package com.plazoleta.usuarios.infrastructure.adapter.input.rest;

import com.plazoleta.usuarios.application.port.in.RegistrarUsuarioPort;
import com.plazoleta.usuarios.application.service.ConsultarUsuarioService;
import com.plazoleta.usuarios.infrastructure.adapter.input.rest.dto.RegistroPropietarioRequest;
import com.plazoleta.usuarios.infrastructure.adapter.input.rest.dto.RegistroUsuarioBasicoRequest;
import com.plazoleta.usuarios.infrastructure.adapter.input.rest.dto.RegistroEmpleadoRequest;
import com.plazoleta.usuarios.infrastructure.adapter.input.rest.dto.UsuarioResponse;
import com.plazoleta.usuarios.infrastructure.adapter.input.rest.dto.UsuarioRolResponse;
import com.plazoleta.usuarios.infrastructure.security.UsuarioPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {
	private final RegistrarUsuarioPort registrarUsuario;
	private final ConsultarUsuarioService consultarUsuario;

	public UsuarioController(RegistrarUsuarioPort registrarUsuario, ConsultarUsuarioService consultarUsuario) {
		this.registrarUsuario = registrarUsuario;
		this.consultarUsuario = consultarUsuario;
	}

	@PostMapping("/propietarios")
	@Operation(summary = "Crear propietario", security = @SecurityRequirement(name = "bearerAuth"))
	@ResponseStatus(HttpStatus.CREATED)
	public UsuarioResponse crearPropietario(@Valid @RequestBody RegistroPropietarioRequest request) {
		var user = registrarUsuario.registrarPropietario(request.nombre(), request.apellido(), request.documento(),
				request.celular(), request.fechaNacimiento(), request.correo(), request.clave());
		return UsuarioResponse.from(user);
	}

	@PostMapping("/clientes")
	@ResponseStatus(HttpStatus.CREATED)
	public UsuarioResponse crearCliente(@Valid @RequestBody RegistroUsuarioBasicoRequest request) {
		return UsuarioResponse.from(registrarUsuario.registrarCliente(request.nombre(), request.apellido(),
				request.documento(), request.celular(), request.correo(), request.clave()));
	}

	@PostMapping("/empleados")
	@Operation(summary = "Crear empleado", security = @SecurityRequirement(name = "bearerAuth"))
	@ResponseStatus(HttpStatus.CREATED)
	public UsuarioResponse crearEmpleado(@Valid @RequestBody RegistroEmpleadoRequest request,
			@AuthenticationPrincipal UsuarioPrincipal propietario, @RequestHeader("Authorization") String bearerToken) {
		return UsuarioResponse.from(registrarUsuario.registrarEmpleado(request.nombre(), request.apellido(),
				request.documento(), request.celular(), request.correo(), request.clave(), request.restauranteId(),
				propietario.id(), bearerToken));
	}

	@GetMapping("/{id}/rol")
	@Operation(summary = "Consultar un rol para validación entre servicios", security = @SecurityRequirement(name = "bearerAuth"))
	public UsuarioRolResponse consultarRol(@PathVariable Long id) {
		var user = consultarUsuario.consultarPorId(id);
		return new UsuarioRolResponse(user.id(), user.rol());
	}
}
