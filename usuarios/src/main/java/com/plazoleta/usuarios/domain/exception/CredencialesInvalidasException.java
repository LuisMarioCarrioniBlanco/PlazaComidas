package com.plazoleta.usuarios.domain.exception;

public class CredencialesInvalidasException extends RuntimeException {
	public CredencialesInvalidasException() {
		super("Credenciales inválidas");
	}
}
