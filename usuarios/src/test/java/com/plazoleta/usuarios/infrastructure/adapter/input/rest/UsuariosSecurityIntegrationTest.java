package com.plazoleta.usuarios.infrastructure.adapter.input.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import tools.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import com.plazoleta.usuarios.application.port.out.RestauranteOwnershipPort;

@SpringBootTest
@AutoConfigureMockMvc
class UsuariosSecurityIntegrationTest {
	@Autowired MockMvc mvc;
	@Autowired JsonMapper objectMapper;
	@MockitoBean RestauranteOwnershipPort restauranteOwnership;

	@Test
	void soloAdministradorPuedeCrearPropietarioYLaRespuestaNoFiltraHash() throws Exception {
		String request = objectMapper.writeValueAsString(Map.of(
				"nombre", "Ana", "apellido", "Pérez", "documento", "12345678", "celular", "+573005698325",
				"fechaNacimiento", "1990-01-01", "correo", "ana@example.com", "clave", "clave-segura"));

		mvc.perform(post("/api/v1/usuarios/propietarios").contentType(MediaType.APPLICATION_JSON).content(request))
				.andExpect(status().isUnauthorized());

		String login = objectMapper.writeValueAsString(Map.of("correo", "admin@example.test", "clave", "admin-test-password"));
		String response = mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON).content(login))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		String token = objectMapper.readTree(response).get("accessToken").asText();

		mvc.perform(post("/api/v1/usuarios/propietarios").header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON).content(request))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.rol").value("PROPIETARIO"))
				.andExpect(jsonPath("$.claveHash").doesNotExist());
	}

	@Test
	void registroPublicoCreaClienteYRechazaRolEnviadoPorElCliente() throws Exception {
		String request = objectMapper.writeValueAsString(Map.of(
				"nombre", "Leo", "apellido", "Díaz", "documento", "98765432", "celular", "3009876543",
				"correo", "leo@example.com", "clave", "clave-segura"));
		mvc.perform(post("/api/v1/usuarios/clientes").contentType(MediaType.APPLICATION_JSON).content(request))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.rol").value("CLIENTE"))
				.andExpect(jsonPath("$.claveHash").doesNotExist());

		String privilegeAttempt = request.substring(0, request.length() - 1) + ",\"rol\":\"ADMIN\"}";
		mvc.perform(post("/api/v1/usuarios/clientes").contentType(MediaType.APPLICATION_JSON).content(privilegeAttempt))
				.andExpect(status().isBadRequest());
	}

	@Test
	void soloAdminConsultaElRolPorIdentificadorParaContratosDeOtrosServicios() throws Exception {
		String login = objectMapper.writeValueAsString(Map.of("correo", "admin@example.test", "clave", "admin-test-password"));
		String response = mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON).content(login))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		String token = objectMapper.readTree(response).get("accessToken").asText();

		mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/v1/usuarios/1/rol")
				.header("Authorization", "Bearer " + token)).andExpect(status().isOk())
				.andExpect(jsonPath("$.rol").value("ADMIN"))
				.andExpect(jsonPath("$.correo").doesNotExist());
	}

	@Test
	void publicaOpenApiYSwagger() throws Exception {
		mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/v3/api-docs"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.paths['/api/v1/usuarios/propietarios']").exists());
	}

	@Test
	void propietarioCreaEmpleadoEnSuRestauranteYElIdRolDelBodyNoElevaPrivilegios() throws Exception {
		String adminLogin = objectMapper.writeValueAsString(Map.of("correo", "admin@example.test", "clave", "admin-test-password"));
		String adminResponse = mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON).content(adminLogin))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		String adminToken = objectMapper.readTree(adminResponse).get("accessToken").asText();
		String ownerBody = objectMapper.writeValueAsString(Map.of("nombre", "Eva", "apellido", "Pérez", "documento", "11223344",
				"celular", "3001234567", "fechaNacimiento", "1990-01-01", "correo", "eva@example.com", "clave", "owner-password"));
		String ownerResponse = mvc.perform(post("/api/v1/usuarios/propietarios").header("Authorization", "Bearer " + adminToken)
				.contentType(MediaType.APPLICATION_JSON).content(ownerBody)).andExpect(status().isCreated())
				.andReturn().getResponse().getContentAsString();
		Long ownerId = objectMapper.readTree(ownerResponse).get("id").asLong();
		String ownerLogin = objectMapper.writeValueAsString(Map.of("correo", "eva@example.com", "clave", "owner-password"));
		String ownerJwt = objectMapper.readTree(mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
				.content(ownerLogin)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString())
				.get("accessToken").asText();
		doNothing().when(restauranteOwnership).verificarPropiedad(eq(21L), eq(ownerId), anyString());
		String employee = objectMapper.writeValueAsString(Map.of("nombre", "Luis", "apellido", "Díaz", "documento", "55667788",
				"celular", "3009876543", "correo", "luis@example.com", "clave", "employee-password", "restauranteId", 21,
				"idRol", 1));
		mvc.perform(post("/api/v1/usuarios/empleados").header("Authorization", "Bearer " + ownerJwt)
				.contentType(MediaType.APPLICATION_JSON).content(employee))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.rol").value("EMPLEADO"));
	}
}
