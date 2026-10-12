package com.plazoleta.plazoleta.application.service;

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
import java.net.URI;
import java.util.Locale;

public class GestionarPlazoletaService {
	private final RestauranteRepositoryPort restaurantes;
	private final PlatoRepositoryPort platos;
	private final UsuarioDirectoryPort usuarios;

	public GestionarPlazoletaService(RestauranteRepositoryPort restaurantes, PlatoRepositoryPort platos,
			UsuarioDirectoryPort usuarios) {
		this.restaurantes = restaurantes;
		this.platos = platos;
		this.usuarios = usuarios;
	}

	public Restaurante crearRestaurante(String nombre, String nit, String direccion, String telefono, String urlLogo,
			Long propietarioId, String bearerToken) {
		requerido(nombre, "El nombre es obligatorio");
		requerido(nit, "El NIT es obligatorio");
		requerido(direccion, "La dirección es obligatoria");
		requerido(telefono, "El teléfono es obligatorio");
		requerido(urlLogo, "La URL del logo es obligatoria");
		if (propietarioId == null || propietarioId < 1) throw new SolicitudInvalidaException("El propietario es obligatorio");
		if (!nit.matches("[0-9]+")) throw new SolicitudInvalidaException("El NIT debe contener solo números");
		if (!telefono.matches("\\+?[0-9]{1,12}")) throw new SolicitudInvalidaException("El teléfono debe contener hasta 13 caracteres numéricos");
		if (nombre.trim().matches("[0-9]+")) throw new SolicitudInvalidaException("El nombre no puede estar compuesto solo por números");
		validarUrl(urlLogo, "La URL del logo");
		if (restaurantes.existePorNit(nit)) throw new ConflictoException("El NIT ya está registrado");
		usuarios.verificarPropietario(propietarioId, bearerToken);
		return restaurantes.guardar(new Restaurante(null, nombre.trim(), nit, direccion.trim(), telefono,
				urlLogo.trim(), propietarioId));
	}

	public Plato crearPlato(Long restauranteId, Long propietarioId, String nombre, Integer precio, String descripcion,
			String urlImagen, String categoria) {
		Restaurante restaurante = obtenerRestaurante(restauranteId);
		verificarPropiedad(restaurante, propietarioId);
		requerido(nombre, "El nombre del plato es obligatorio");
		requerido(descripcion, "La descripción es obligatoria");
		requerido(urlImagen, "La URL de la imagen es obligatoria");
		requerido(categoria, "La categoría es obligatoria");
		if (precio == null || precio <= 0) throw new SolicitudInvalidaException("El precio debe ser un entero positivo mayor que cero");
		validarUrl(urlImagen, "La URL de la imagen");
		return platos.guardar(new Plato(null, nombre.trim(), precio, descripcion.trim(), urlImagen.trim(),
				categoria.trim(), restaurante.id(), true));
	}

	public Plato modificarPlato(Long platoId, Long propietarioId, Integer precio, String descripcion) {
		Plato plato = obtenerPlato(platoId);
		Restaurante restaurante = obtenerRestaurante(plato.restauranteId());
		verificarPropiedad(restaurante, propietarioId);
		if (precio == null || precio <= 0) throw new SolicitudInvalidaException("El precio debe ser un entero positivo mayor que cero");
		requerido(descripcion, "La descripción es obligatoria");
		return platos.guardar(new Plato(plato.id(), plato.nombre(), precio, descripcion.trim(), plato.urlImagen(),
				plato.categoria(), plato.restauranteId(), plato.activo()));
	}

	public Plato cambiarEstado(Long platoId, Long propietarioId, Boolean activo) {
		if (activo == null) throw new SolicitudInvalidaException("El estado activo es obligatorio");
		Plato plato = obtenerPlato(platoId);
		Restaurante restaurante = obtenerRestaurante(plato.restauranteId());
		verificarPropiedad(restaurante, propietarioId);
		return platos.guardar(new Plato(plato.id(), plato.nombre(), plato.precio(), plato.descripcion(),
				plato.urlImagen(), plato.categoria(), plato.restauranteId(), activo));
	}

	public ResultadoPaginado<Restaurante> listarRestaurantes(int pagina, int tamano) {
		if (pagina < 0 || tamano < 1 || tamano > 100) {
			throw new SolicitudInvalidaException("La página debe ser >= 0 y el tamaño debe estar entre 1 y 100");
		}
		return restaurantes.listarOrdenados(pagina, tamano);
	}

	public void verificarPropiedadParaEmpleado(Long restauranteId, Long propietarioId) {
		verificarPropiedad(obtenerRestaurante(restauranteId), propietarioId);
	}

	private Restaurante obtenerRestaurante(Long id) {
		if (id == null || id < 1) throw new SolicitudInvalidaException("El restaurante es obligatorio");
		return restaurantes.buscarPorId(id).orElseThrow(() -> new NoEncontradoException("El restaurante no existe"));
	}

	private Plato obtenerPlato(Long id) {
		if (id == null || id < 1) throw new SolicitudInvalidaException("El plato es obligatorio");
		return platos.buscarPorId(id).orElseThrow(() -> new NoEncontradoException("El plato no existe"));
	}

	private void verificarPropiedad(Restaurante restaurante, Long propietarioId) {
		if (propietarioId == null || !restaurante.propietarioId().equals(propietarioId)) {
			throw new AccesoDenegadoException("El propietario no administra ese restaurante");
		}
	}

	private void validarUrl(String valor, String campo) {
		try {
			URI uri = URI.create(valor.trim());
			String scheme = uri.getScheme();
			if (!uri.isAbsolute() || uri.getHost() == null || scheme == null
					|| !(scheme.toLowerCase(Locale.ROOT).equals("http") || scheme.toLowerCase(Locale.ROOT).equals("https"))) {
				throw new IllegalArgumentException();
			}
		} catch (IllegalArgumentException exception) {
			throw new SolicitudInvalidaException(campo + " debe ser HTTP o HTTPS y válida");
		}
	}

	private void requerido(String valor, String mensaje) {
		if (valor == null || valor.isBlank()) throw new SolicitudInvalidaException(mensaje);
	}
}
