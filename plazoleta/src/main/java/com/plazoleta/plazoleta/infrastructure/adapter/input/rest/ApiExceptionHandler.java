package com.plazoleta.plazoleta.infrastructure.adapter.input.rest;

import com.plazoleta.plazoleta.domain.exception.AccesoDenegadoException;
import com.plazoleta.plazoleta.domain.exception.ConflictoException;
import com.plazoleta.plazoleta.domain.exception.NoEncontradoException;
import com.plazoleta.plazoleta.domain.exception.SolicitudInvalidaException;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
	@ExceptionHandler({SolicitudInvalidaException.class, MethodArgumentNotValidException.class})
	ResponseEntity<Map<String, String>> solicitudInvalida(Exception exception) {
		String message = exception instanceof SolicitudInvalidaException ? exception.getMessage()
				: "La solicitud contiene campos inválidos o faltantes";
		return ResponseEntity.badRequest().body(Map.of("mensaje", message));
	}
	@ExceptionHandler({ConflictoException.class, DataIntegrityViolationException.class})
	ResponseEntity<Map<String, String>> conflicto(Exception exception) {
		String message = exception instanceof ConflictoException ? exception.getMessage() : "El registro ya existe";
		return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("mensaje", message));
	}
	@ExceptionHandler(NoEncontradoException.class)
	ResponseEntity<Map<String, String>> noEncontrado(NoEncontradoException exception) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", exception.getMessage()));
	}
	@ExceptionHandler(AccesoDenegadoException.class)
	ResponseEntity<Map<String, String>> accesoDenegado(AccesoDenegadoException exception) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("mensaje", exception.getMessage()));
	}
	@ExceptionHandler(IllegalStateException.class)
	ResponseEntity<Map<String, String>> dependenciaNoDisponible(IllegalStateException exception) {
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
				.body(Map.of("mensaje", "No está disponible un servicio requerido"));
	}
}
