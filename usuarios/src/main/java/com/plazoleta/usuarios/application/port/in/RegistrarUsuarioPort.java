package com.plazoleta.usuarios.application.port.in;

import com.plazoleta.usuarios.domain.model.Usuario;
import java.time.LocalDate;

public interface RegistrarUsuarioPort {
	Usuario registrarPropietario(String nombre, String apellido, String documento, String celular,
			LocalDate fechaNacimiento, String correo, String clave);
	Usuario registrarCliente(String nombre, String apellido, String documento, String celular, String correo, String clave);
	Usuario registrarEmpleado(String nombre, String apellido, String documento, String celular, String correo,
			String clave, Long restauranteId, Long propietarioId, String bearerToken);
}
