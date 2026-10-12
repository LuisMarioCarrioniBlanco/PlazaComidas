package com.plazoleta.usuarios.infrastructure.config;

import com.plazoleta.usuarios.application.port.in.RegistrarUsuarioPort;
import com.plazoleta.usuarios.application.port.out.PasswordHasherPort;
import com.plazoleta.usuarios.application.port.out.RestauranteOwnershipPort;
import com.plazoleta.usuarios.application.port.out.UsuarioRepositoryPort;
import com.plazoleta.usuarios.application.service.RegistrarUsuarioService;
import com.plazoleta.usuarios.application.service.AutenticarUsuarioService;
import com.plazoleta.usuarios.application.service.ConsultarUsuarioService;
import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfiguration {
	@Bean
	Clock clock() {
		return Clock.systemDefaultZone();
	}

	@Bean
	RegistrarUsuarioPort registrarUsuario(UsuarioRepositoryPort repository, PasswordHasherPort hasher, Clock clock,
			RestauranteOwnershipPort restauranteOwnership) {
		return new RegistrarUsuarioService(repository, hasher, clock, restauranteOwnership);
	}

	@Bean
	AutenticarUsuarioService autenticarUsuario(UsuarioRepositoryPort repository, PasswordHasherPort hasher) {
		return new AutenticarUsuarioService(repository, hasher);
	}

	@Bean
	ConsultarUsuarioService consultarUsuario(UsuarioRepositoryPort repository) {
		return new ConsultarUsuarioService(repository);
	}
}
