package com.plazoleta.plazoleta.infrastructure.adapter.output.http;

import com.plazoleta.plazoleta.application.port.out.UsuarioDirectoryPort;
import com.plazoleta.plazoleta.domain.exception.AccesoDenegadoException;
import com.plazoleta.plazoleta.domain.exception.SolicitudInvalidaException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
public class UsuarioDirectoryHttpAdapter implements UsuarioDirectoryPort {
	private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build();
	private final String usuariosUrl;
	private final JsonMapper jsonMapper;

	public UsuarioDirectoryHttpAdapter(@Value("${services.usuarios.url}") String usuariosUrl, JsonMapper jsonMapper) {
		this.usuariosUrl = usuariosUrl.replaceAll("/$", "");
		this.jsonMapper = jsonMapper;
	}

	@Override
	public void verificarPropietario(Long usuarioId, String bearerToken) {
		URI uri = URI.create(usuariosUrl + "/api/v1/usuarios/" + usuarioId + "/rol");
		HttpRequest request = HttpRequest.newBuilder(uri).timeout(Duration.ofSeconds(3))
				.header("Authorization", bearerToken).GET().build();
		try {
			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() == 404) throw new SolicitudInvalidaException("El usuario indicado no existe");
			if (response.statusCode() == 401 || response.statusCode() == 403) {
				throw new AccesoDenegadoException("Se requiere una identidad de administrador para validar el propietario");
			}
			if (response.statusCode() != 200) throw new IllegalStateException("No se pudo verificar el usuario");
			String role = jsonMapper.readTree(response.body()).path("rol").asString();
			if (!"PROPIETARIO".equals(role)) throw new SolicitudInvalidaException("El usuario indicado no tiene rol PROPIETARIO");
		} catch (InterruptedException exception) {
			Thread.currentThread().interrupt();
			throw new IllegalStateException("No se pudo verificar el usuario", exception);
		} catch (IOException exception) {
			throw new IllegalStateException("No se pudo conectar con el microservicio Usuarios", exception);
		}
	}
}
