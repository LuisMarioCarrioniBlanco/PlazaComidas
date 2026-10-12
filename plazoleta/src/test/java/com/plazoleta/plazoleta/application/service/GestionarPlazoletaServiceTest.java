package com.plazoleta.plazoleta.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.plazoleta.plazoleta.application.port.out.PlatoRepositoryPort;
import com.plazoleta.plazoleta.application.port.out.RestauranteRepositoryPort;
import com.plazoleta.plazoleta.application.port.out.UsuarioDirectoryPort;
import com.plazoleta.plazoleta.domain.exception.AccesoDenegadoException;
import com.plazoleta.plazoleta.domain.exception.ConflictoException;
import com.plazoleta.plazoleta.domain.exception.NoEncontradoException;
import com.plazoleta.plazoleta.domain.exception.SolicitudInvalidaException;
import com.plazoleta.plazoleta.domain.model.Plato;
import com.plazoleta.plazoleta.domain.model.Restaurante;
import com.plazoleta.plazoleta.domain.model.ResultadoPaginado;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GestionarPlazoletaServiceTest {
	@Mock RestauranteRepositoryPort restaurantes;
	@Mock PlatoRepositoryPort platos;
	@Mock UsuarioDirectoryPort usuarios;
	GestionarPlazoletaService service;
	private final Restaurante restaurant = new Restaurante(4L, "Arepas 21", "900123", "Calle 1", "+573001234567",
			"https://example.com/logo.png", 8L);
	private final Plato plate = new Plato(12L, "Arepa", 12000, "Maíz", "https://example.com/arepa.png", "Desayuno", 4L, true);

	@BeforeEach void setUp() { service = new GestionarPlazoletaService(restaurantes, platos, usuarios); }

	@Test
	void creaRestauranteTrasVerificarPropietario() {
		when(restaurantes.existePorNit("900123")).thenReturn(false);
		doNothing().when(usuarios).verificarPropietario(8L, "Bearer admin");
		when(restaurantes.guardar(any())).thenAnswer(invocation -> invocation.getArgument(0));

		Restaurante created = service.crearRestaurante("Arepas 21", "900123", "Calle 1", "+573001234567",
				"https://example.com/logo.png", 8L, "Bearer admin");

		assertEquals(8L, created.propietarioId());
		verify(usuarios).verificarPropietario(8L, "Bearer admin");
	}

	@Test
	void rechazaNombreSoloNumericoYNitDuplicado() {
		assertThrows(SolicitudInvalidaException.class, () -> service.crearRestaurante("123", "900123", "Calle 1",
				"3001234567", "https://example.com/logo.png", 8L, "Bearer admin"));
		when(restaurantes.existePorNit("900123")).thenReturn(true);
		assertThrows(ConflictoException.class, () -> service.crearRestaurante("Arepas", "900123", "Calle 1",
				"3001234567", "https://example.com/logo.png", 8L, "Bearer admin"));
	}

	@Test
	void creaPlatoActivoEnRestaurantePropio() {
		when(restaurantes.buscarPorId(4L)).thenReturn(Optional.of(restaurant));
		when(platos.guardar(any())).thenAnswer(invocation -> invocation.getArgument(0));

		Plato created = service.crearPlato(4L, 8L, "Arepa", 12000, "Maíz", "https://example.com/arepa.png", "Desayuno");

		assertTrue(created.activo());
		assertEquals(4L, created.restauranteId());
	}

	@Test
	void impideCrearPlatoParaRestauranteAjeno() {
		when(restaurantes.buscarPorId(4L)).thenReturn(Optional.of(restaurant));
		assertThrows(AccesoDenegadoException.class, () -> service.crearPlato(4L, 9L, "Arepa", 12000,
				"Maíz", "https://example.com/arepa.png", "Desayuno"));
		verify(platos, never()).guardar(any());
	}

	@Test
	void modificarSoloPrecioYDescripcionConservaOtrosCampos() {
		when(platos.buscarPorId(12L)).thenReturn(Optional.of(plate));
		when(restaurantes.buscarPorId(4L)).thenReturn(Optional.of(restaurant));
		when(platos.guardar(any())).thenAnswer(invocation -> invocation.getArgument(0));

		Plato updated = service.modificarPlato(12L, 8L, 15000, "Maíz dulce");

		assertEquals("Arepa", updated.nombre());
		assertEquals("https://example.com/arepa.png", updated.urlImagen());
		assertEquals("Desayuno", updated.categoria());
		assertEquals(4L, updated.restauranteId());
		assertEquals(15000, updated.precio());
		assertEquals("Maíz dulce", updated.descripcion());
	}

	@Test
	void estadoSoloPuedeCambiarloElPropietario() {
		when(platos.buscarPorId(12L)).thenReturn(Optional.of(plate));
		when(restaurantes.buscarPorId(4L)).thenReturn(Optional.of(restaurant));
		when(platos.guardar(any())).thenAnswer(invocation -> invocation.getArgument(0));
		assertFalse(service.cambiarEstado(12L, 8L, false).activo());
		assertThrows(AccesoDenegadoException.class, () -> service.cambiarEstado(12L, 9L, true));
	}

	@Test
	void informaRecursoNoEncontrado() {
		when(platos.buscarPorId(99L)).thenReturn(Optional.empty());
		assertThrows(NoEncontradoException.class, () -> service.modificarPlato(99L, 8L, 1000, "Descripción"));
	}

	@Test
	void validaYPaginaListado() {
		var page = new ResultadoPaginado<>(List.of(restaurant), 0, 1, 1, 1);
		when(restaurantes.listarOrdenados(0, 1)).thenReturn(page);
		assertEquals("Arepas 21", service.listarRestaurantes(0, 1).contenido().getFirst().nombre());
		assertThrows(SolicitudInvalidaException.class, () -> service.listarRestaurantes(-1, 10));
		assertThrows(SolicitudInvalidaException.class, () -> service.listarRestaurantes(0, 101));
	}
}
