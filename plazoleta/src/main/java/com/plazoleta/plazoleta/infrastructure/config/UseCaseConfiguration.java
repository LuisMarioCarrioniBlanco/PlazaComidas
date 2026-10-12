package com.plazoleta.plazoleta.infrastructure.config;

import com.plazoleta.plazoleta.application.port.out.PlatoRepositoryPort;
import com.plazoleta.plazoleta.application.port.out.RestauranteRepositoryPort;
import com.plazoleta.plazoleta.application.port.out.UsuarioDirectoryPort;
import com.plazoleta.plazoleta.application.service.GestionarPlazoletaService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfiguration {
	@Bean
	GestionarPlazoletaService gestionarPlazoleta(RestauranteRepositoryPort restaurantes, PlatoRepositoryPort platos,
			UsuarioDirectoryPort usuarios) {
		return new GestionarPlazoletaService(restaurantes, platos, usuarios);
	}
}
