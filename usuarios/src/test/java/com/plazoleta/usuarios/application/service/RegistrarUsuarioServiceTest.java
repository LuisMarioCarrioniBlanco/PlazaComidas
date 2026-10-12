package com.plazoleta.usuarios.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.plazoleta.usuarios.application.port.out.PasswordHasherPort;
import com.plazoleta.usuarios.application.port.out.RestauranteOwnershipPort;
import com.plazoleta.usuarios.application.port.out.UsuarioRepositoryPort;
import com.plazoleta.usuarios.domain.exception.DatosDuplicadosException;
import com.plazoleta.usuarios.domain.exception.DatosInvalidosException;
import com.plazoleta.usuarios.domain.exception.AccesoDenegadoException;
import com.plazoleta.usuarios.domain.model.Rol;
import com.plazoleta.usuarios.domain.model.Usuario;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegistrarUsuarioServiceTest {
	@Mock UsuarioRepositoryPort usuarios;
	@Mock PasswordHasherPort hasher;
	@Mock RestauranteOwnershipPort restauranteOwnership;
	RegistrarUsuarioService service;

	@BeforeEach
	void setUp() {
		service = new RegistrarUsuarioService(usuarios, hasher, Clock.fixed(Instant.parse("2026-10-11T12:00:00Z"), ZoneOffset.UTC),
				restauranteOwnership);
	}

	@Test
	void registraPropietarioMayorDeEdadConClaveCifradaYRolFijo() {
		when(usuarios.existePorCorreo("ana@example.com")).thenReturn(false);
		when(usuarios.existePorDocumento("12345678")).thenReturn(false);
		when(hasher.hash("clave" )).thenReturn("$2a$hash");
		when(usuarios.guardar(any())).thenAnswer(invocation -> invocation.getArgument(0));

		Usuario user = service.registrarPropietario(" Ana ", " Pérez ", "12345678", "+573005698325",
				LocalDate.of(2008, 10, 11), "ANA@example.com", "clave");

		assertEquals(Rol.PROPIETARIO, user.rol());
		assertEquals("ana@example.com", user.correo());
		assertEquals("$2a$hash", user.claveHash());
		assertNotEquals("clave", user.claveHash());
	}

	@Test
	void rechazaMenorDeEdad() {
		assertThrows(DatosInvalidosException.class, () -> service.registrarPropietario("Ana", "Pérez", "12345678",
				"3001234567", LocalDate.of(2008, 10, 12), "ana@example.com", "clave"));
		verify(usuarios, never()).guardar(any());
	}

	@Test
	void rechazaDocumentoDuplicado() {
		when(usuarios.existePorCorreo("ana@example.com")).thenReturn(false);
		when(usuarios.existePorDocumento("12345678")).thenReturn(true);

		assertThrows(DatosDuplicadosException.class, () -> service.registrarPropietario("Ana", "Pérez", "12345678",
				"3001234567", LocalDate.of(1990, 1, 1), "ana@example.com", "clave"));
		verify(usuarios, never()).guardar(any());
	}

	@Test
	void rechazaCorreoDuplicadoSinFiltrarExistenciaDeDocumento() {
		when(usuarios.existePorCorreo("ana@example.com")).thenReturn(true);

		assertThrows(DatosDuplicadosException.class, () -> service.registrarPropietario("Ana", "Pérez", "12345678",
				"3001234567", LocalDate.of(1990, 1, 1), "ana@example.com", "clave"));
		verify(usuarios, never()).existePorDocumento(any());
	}

	@Test
	void validaDocumentoTelefonoCorreoYCamposObligatorios() {
		assertThrows(DatosInvalidosException.class, () -> service.registrarPropietario("Ana", "Pérez", "12A",
				"3001234567", LocalDate.of(1990, 1, 1), "ana@example.com", "clave"));
		assertThrows(DatosInvalidosException.class, () -> service.registrarPropietario("Ana", "Pérez", "123",
				"+5730056983255", LocalDate.of(1990, 1, 1), "ana@example.com", "clave"));
		assertThrows(DatosInvalidosException.class, () -> service.registrarPropietario("Ana", "Pérez", "123",
				"3001234567", LocalDate.of(1990, 1, 1), "sin-correo", "clave"));
		assertThrows(DatosInvalidosException.class, () -> service.registrarPropietario(" ", "Pérez", "123",
				"3001234567", LocalDate.of(1990, 1, 1), "ana@example.com", "clave"));
	}

	@Test
	void asignaRolClienteDesdeElServidor() {
		when(usuarios.existePorCorreo("cliente@example.com")).thenReturn(false);
		when(usuarios.existePorDocumento("12345678")).thenReturn(false);
		when(hasher.hash("clave")).thenReturn("bcrypt-hash");
		when(usuarios.guardar(any())).thenAnswer(invocation -> invocation.getArgument(0));

		Usuario user = service.registrarCliente("Ana", "Pérez", "12345678", "3001234567", "CLIENTE@example.com", "clave");

		assertEquals(Rol.CLIENTE, user.rol());
	}

	@Test
	void empleadoSoloSeCreaTrasConfirmarPropiedadDelRestaurante() {
		doNothing().when(restauranteOwnership).verificarPropiedad(42L, 9L, "Bearer owner-token");
		when(usuarios.existePorCorreo("empleado@example.com")).thenReturn(false);
		when(usuarios.existePorDocumento("12345678")).thenReturn(false);
		when(hasher.hash("clave")).thenReturn("bcrypt-hash");
		when(usuarios.guardar(any())).thenAnswer(invocation -> invocation.getArgument(0));

		Usuario user = service.registrarEmpleado("Eva", "Pérez", "12345678", "3001234567", "empleado@example.com",
				"clave", 42L, 9L, "Bearer owner-token");

		assertEquals(Rol.EMPLEADO, user.rol());
		assertEquals(42L, user.restauranteId());
	}

	@Test
	void empleadoDeOtroPropietarioNoSeCrea() {
		doThrow(new AccesoDenegadoException("sin acceso"))
				.when(restauranteOwnership).verificarPropiedad(42L, 9L, "Bearer owner-token");

		assertThrows(AccesoDenegadoException.class, () -> service.registrarEmpleado("Eva", "Pérez", "12345678",
				"3001234567", "empleado@example.com", "clave", 42L, 9L, "Bearer owner-token"));
		verify(usuarios, never()).guardar(any());
	}
}
