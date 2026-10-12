package com.plazoleta.usuarios.domain.exception;

public class RestauranteNoEncontradoException extends RuntimeException {
	public RestauranteNoEncontradoException() { super("El restaurante no existe"); }
}
