package com.plazoleta.usuarios.domain.exception;

public class UsuarioNoEncontradoException extends RuntimeException {
	public UsuarioNoEncontradoException() { super("El usuario no existe"); }
}
