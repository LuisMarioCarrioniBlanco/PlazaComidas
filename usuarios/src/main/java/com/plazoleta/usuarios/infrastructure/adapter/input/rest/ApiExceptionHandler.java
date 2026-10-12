package com.plazoleta.usuarios.infrastructure.adapter.input.rest;

import com.plazoleta.usuarios.domain.exception.DatosDuplicadosException;
import com.plazoleta.usuarios.domain.exception.DatosInvalidosException;
import com.plazoleta.usuarios.domain.exception.CredencialesInvalidasException;
import com.plazoleta.usuarios.domain.exception.AccesoDenegadoException;
import com.plazoleta.usuarios.domain.exception.RestauranteNoEncontradoException;
import com.plazoleta.usuarios.domain.exception.UsuarioNoEncontradoException;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
	@ExceptionHandler(DatosInvalidosException.class)
	ResponseEntity<Map<String, String>> datosInvalidos(RuntimeException exception) {
		return ResponseEntity.badRequest().body(Map.of("mensaje", exception.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	ResponseEntity<Map<String, String>> validacion(MethodArgumentNotValidException exception) {
		String message = exception.getBindingResult().getFieldErrors().stream()
				.map(error -> error.getField() + ": dato inválido o faltante")
				.findFirst().orElse("La solicitud contiene datos inválidos");
		return ResponseEntity.badRequest().body(Map.of("mensaje", message));
	}

	@ExceptionHandler({DatosDuplicadosException.class, DataIntegrityViolationException.class})
	ResponseEntity<Map<String, String>> duplicado(Exception exception) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("mensaje", "El correo o documento ya está registrado"));
	}

	@ExceptionHandler(CredencialesInvalidasException.class)
	ResponseEntity<Map<String, String>> credencialesInvalidas(CredencialesInvalidasException exception) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("mensaje", exception.getMessage()));
	}

	@ExceptionHandler(AccesoDenegadoException.class)
	ResponseEntity<Map<String, String>> accesoDenegado(AccesoDenegadoException exception) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("mensaje", exception.getMessage()));
	}

	@ExceptionHandler(RestauranteNoEncontradoException.class)
	ResponseEntity<Map<String, String>> restauranteNoEncontrado(RestauranteNoEncontradoException exception) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", exception.getMessage()));
	}

	@ExceptionHandler(UsuarioNoEncontradoException.class)
	ResponseEntity<Map<String, String>> usuarioNoEncontrado(UsuarioNoEncontradoException exception) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", exception.getMessage()));
	}
}
