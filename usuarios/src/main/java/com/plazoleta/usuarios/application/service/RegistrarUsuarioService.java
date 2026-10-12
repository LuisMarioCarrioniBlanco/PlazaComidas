package com.plazoleta.usuarios.application.service;

import com.plazoleta.usuarios.application.port.in.RegistrarUsuarioPort;
import com.plazoleta.usuarios.application.port.out.PasswordHasherPort;
import com.plazoleta.usuarios.application.port.out.RestauranteOwnershipPort;
import com.plazoleta.usuarios.application.port.out.UsuarioRepositoryPort;
import com.plazoleta.usuarios.application.port.out.RestauranteOwnershipPort;
import com.plazoleta.usuarios.domain.exception.DatosDuplicadosException;
import com.plazoleta.usuarios.domain.exception.DatosInvalidosException;
import com.plazoleta.usuarios.domain.model.Rol;
import com.plazoleta.usuarios.domain.model.Usuario;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Locale;

public class RegistrarUsuarioService implements RegistrarUsuarioPort {
	private final UsuarioRepositoryPort usuarios;
	private final PasswordHasherPort passwordHasher;
	private final Clock clock;
	private final RestauranteOwnershipPort restauranteOwnership;

	public RegistrarUsuarioService(UsuarioRepositoryPort usuarios, PasswordHasherPort passwordHasher, Clock clock,
			RestauranteOwnershipPort restauranteOwnership) {
		this.usuarios = usuarios;
		this.passwordHasher = passwordHasher;
		this.clock = clock;
		this.restauranteOwnership = restauranteOwnership;
	}

	@Override
	public Usuario registrarPropietario(String nombre, String apellido, String documento, String celular,
			LocalDate fechaNacimiento, String correo, String clave) {
		validarObligatorios(nombre, apellido, documento, celular, correo, clave);
		if (fechaNacimiento == null || !fechaNacimiento.isBefore(LocalDate.now(clock).minusYears(18).plusDays(1))) {
			throw new DatosInvalidosException("El propietario debe ser mayor de edad");
		}
		return registrarUsuario(nombre, apellido, documento, celular, correo, clave, Rol.PROPIETARIO, null, fechaNacimiento);
	}

	@Override
	public Usuario registrarCliente(String nombre, String apellido, String documento, String celular, String correo,
			String clave) {
		return registrarUsuario(nombre, apellido, documento, celular, correo, clave, Rol.CLIENTE, null, null);
	}

	@Override
	public Usuario registrarEmpleado(String nombre, String apellido, String documento, String celular, String correo,
			String clave, Long restauranteId, Long propietarioId, String bearerToken) {
		if (restauranteId == null || restauranteId < 1) {
			throw new DatosInvalidosException("Debe indicar un restaurante válido");
		}
		if (propietarioId == null || bearerToken == null || bearerToken.isBlank()) {
			throw new DatosInvalidosException("Se requiere una identidad autenticada");
		}
		restauranteOwnership.verificarPropiedad(restauranteId, propietarioId, bearerToken);
		return registrarUsuario(nombre, apellido, documento, celular, correo, clave, Rol.EMPLEADO, restauranteId, null);
	}

	private Usuario registrarUsuario(String nombre, String apellido, String documento, String celular, String correo,
			String clave, Rol rol, Long restauranteId, LocalDate fechaNacimiento) {
		validarObligatorios(nombre, apellido, documento, celular, correo, clave);
		if (!documento.matches("[0-9]+")) {
			throw new DatosInvalidosException("El documento debe contener solo números");
		}
		if (!celular.matches("\\+?[0-9]{1,12}") || celular.length() > 13) {
			throw new DatosInvalidosException("El celular debe tener hasta 13 caracteres y solo admitir un + inicial");
		}
		String email = correo.trim().toLowerCase(Locale.ROOT);
		if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
			throw new DatosInvalidosException("El correo no tiene un formato válido");
		}
		if (usuarios.existePorCorreo(email)) {
			throw new DatosDuplicadosException("El correo ya está registrado");
		}
		if (usuarios.existePorDocumento(documento)) {
			throw new DatosDuplicadosException("El documento ya está registrado");
		}
		return usuarios.guardar(new Usuario(null, nombre.trim(), apellido.trim(), documento, celular,
				fechaNacimiento, email, passwordHasher.hash(clave), rol, restauranteId));
	}

	private void validarObligatorios(String nombre, String apellido, String documento, String celular, String correo,
			String clave) {
		if (esVacio(nombre) || esVacio(apellido) || esVacio(documento) || esVacio(celular) || esVacio(correo)
				|| esVacio(clave)) {
			throw new DatosInvalidosException("Todos los campos son obligatorios");
		}
	}

	private boolean esVacio(String valor) {
		return valor == null || valor.isBlank();
	}
}
