package com.plazoleta.plazoleta.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfiguration {
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws Exception {
		return http.csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.exceptionHandling(errors -> errors.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/v1/restaurantes").hasRole("ADMIN")
						.requestMatchers(HttpMethod.GET, "/api/v1/restaurantes").hasRole("CLIENTE")
						.requestMatchers(HttpMethod.GET, "/api/v1/internal/restaurantes/**").hasRole("PROPIETARIO")
						.requestMatchers(HttpMethod.POST, "/api/v1/restaurantes/*/platos").hasRole("PROPIETARIO")
						.requestMatchers(HttpMethod.PATCH, "/api/v1/platos/*").hasRole("PROPIETARIO")
						.requestMatchers(HttpMethod.PUT, "/api/v1/platos/*/estado").hasRole("PROPIETARIO")
						.anyRequest().authenticated())
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
				.build();
	}

	@Bean
	UserDetailsService noFrameworkManagedUsers() {
		return username -> { throw new UsernameNotFoundException("La autenticación se resuelve mediante JWT"); };
	}
}
