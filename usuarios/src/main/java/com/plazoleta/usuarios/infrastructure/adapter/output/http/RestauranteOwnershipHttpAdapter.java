package com.plazoleta.usuarios.infrastructure.adapter.output.http;

import com.plazoleta.usuarios.application.port.out.RestauranteOwnershipPort;
import com.plazoleta.usuarios.domain.exception.AccesoDenegadoException;
import com.plazoleta.usuarios.domain.exception.RestauranteNoEncontradoException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RestauranteOwnershipHttpAdapter implements RestauranteOwnershipPort {
	private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build();
	private final String plazoletaUrl;

	public RestauranteOwnershipHttpAdapter(@Value("${services.plazoleta.url:http://localhost:8082}") String plazoletaUrl) {
		this.plazoletaUrl = plazoletaUrl.replaceAll("/$", "");
	}

	@Override
	public void verificarPropiedad(Long restauranteId, Long propietarioId, String bearerToken) {
		URI uri = URI.create(plazoletaUrl + "/api/v1/internal/restaurantes/" + restauranteId
				+ "/propietarios/" + propietarioId);
		HttpRequest request = HttpRequest.newBuilder(uri).timeout(Duration.ofSeconds(3))
				.header("Authorization", bearerToken).GET().build();
		try {
			int status = httpClient.send(request, HttpResponse.BodyHandlers.discarding()).statusCode();
			if (status == 204) return;
			if (status == 404) throw new RestauranteNoEncontradoException();
			if (status == 401 || status == 403) throw new AccesoDenegadoException("El propietario no administra ese restaurante");
			throw new IllegalStateException("No se pudo verificar la relación con el restaurante");
		} catch (InterruptedException exception) {
			Thread.currentThread().interrupt();
			throw new IllegalStateException("No se pudo verificar la relación con el restaurante", exception);
		} catch (IOException exception) {
			throw new IllegalStateException("No se pudo conectar con el microservicio Plazoleta", exception);
		}
	}
}
