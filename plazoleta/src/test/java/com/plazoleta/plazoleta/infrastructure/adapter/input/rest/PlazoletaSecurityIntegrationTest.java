package com.plazoleta.plazoleta.infrastructure.adapter.input.rest;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.plazoleta.plazoleta.application.port.out.UsuarioDirectoryPort;
import com.plazoleta.plazoleta.infrastructure.adapter.output.persistence.RestauranteJpaEntity;
import com.plazoleta.plazoleta.infrastructure.adapter.output.persistence.PlatoJpaEntity;
import com.plazoleta.plazoleta.infrastructure.adapter.output.persistence.SpringDataPlatoRepository;
import com.plazoleta.plazoleta.infrastructure.adapter.output.persistence.SpringDataRestauranteRepository;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import tools.jackson.databind.json.JsonMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@SpringBootTest
@AutoConfigureMockMvc
class PlazoletaSecurityIntegrationTest {
	@Autowired MockMvc mvc;
	@Autowired JsonMapper jsonMapper;
	@Autowired SpringDataRestauranteRepository restaurantes;
	@Autowired SpringDataPlatoRepository platos;
	@MockitoBean UsuarioDirectoryPort usuarioDirectory;
	@Value("${app.jwt.secret}") String jwtSecret;
	private Long ownerRestaurantId;

	@BeforeEach
	void seed() {
		platos.deleteAll();
		restaurantes.deleteAll();
		var saved = restaurantes.saveAll(java.util.List.of(
				new RestauranteJpaEntity(null, "Zeta", "9003", "Calle 3", "3000000003", "https://example.com/z.png", 8L),
				new RestauranteJpaEntity(null, "Alfa", "9001", "Calle 1", "3000000001", "https://example.com/a.png", 8L),
				new RestauranteJpaEntity(null, "Bravo", "9002", "Calle 2", "3000000002", "https://example.com/b.png", 8L)));
		ownerRestaurantId = saved.getFirst().getId();
		platos.save(new PlatoJpaEntity(null, "Arepa", 12000, "Maíz", "https://example.com/arepa.png", "Desayuno",
				saved.getFirst(), true));
	}

	@Test
	void listaSoloNombreLogoEnOrdenAscendenteYConPaginacion() throws Exception {
		mvc.perform(get("/api/v1/restaurantes?page=0&size=2").header("Authorization", token(100L, "CLIENTE")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content.length()").value(2))
				.andExpect(jsonPath("$.content[0].nombre").value("Alfa"))
				.andExpect(jsonPath("$.content[0].urlLogo").value("https://example.com/a.png"))
				.andExpect(jsonPath("$.content[0].nit").doesNotExist())
				.andExpect(jsonPath("$.totalElements").value(3))
				.andExpect(jsonPath("$.totalPages").value(2));
		mvc.perform(get("/api/v1/restaurantes?page=1&size=2").header("Authorization", token(100L, "CLIENTE")))
				.andExpect(status().isOk()).andExpect(jsonPath("$.content[0].nombre").value("Zeta"));
	}

	@Test
	void protegeLaListaPorRolYValidaPaginacion() throws Exception {
		mvc.perform(get("/api/v1/restaurantes")).andExpect(status().isUnauthorized());
		mvc.perform(get("/api/v1/restaurantes").header("Authorization", token(8L, "PROPIETARIO")))
				.andExpect(status().isForbidden());
		mvc.perform(get("/api/v1/restaurantes?page=-1").header("Authorization", token(100L, "CLIENTE")))
				.andExpect(status().isBadRequest());
		mvc.perform(get("/api/v1/restaurantes?size=101").header("Authorization", token(100L, "CLIENTE")))
				.andExpect(status().isBadRequest());
	}

	@Test
	void creaRestauranteSoloAdminYValidaPropietarioPorContratoHttpSimulado() throws Exception {
		doNothing().when(usuarioDirectory).verificarPropietario(8L, token(1L, "ADMIN"));
		String body = jsonMapper.writeValueAsString(Map.of("nombre", "Comidas 24", "nit", "900777", "direccion", "Calle 7",
				"telefono", "+573001234567", "urlLogo", "https://example.com/logo.png", "propietarioId", 8));
		mvc.perform(post("/api/v1/restaurantes").header("Authorization", token(1L, "ADMIN"))
				.contentType(MediaType.APPLICATION_JSON).content(body))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.nombre").value("Comidas 24"))
				.andExpect(jsonPath("$.nit").doesNotExist());
	}

	@Test
	void propietarioSoloCambiaPlatosDeSuRestauranteYSoloSeModificanPrecioDescripcion() throws Exception {
		Long dishId = platos.findAll().getFirst().getId();
		String createBody = "{\"nombre\":\"Café\",\"precio\":5000,\"descripcion\":\"Tinto\","
				+ "\"urlImagen\":\"https://example.com/cafe.png\",\"categoria\":\"Bebidas\"}";
		mvc.perform(post("/api/v1/restaurantes/" + ownerRestaurantId + "/platos")
				.header("Authorization", token(8L, "PROPIETARIO")).contentType(MediaType.APPLICATION_JSON).content(createBody))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.activo").value(true));

		String update = "{\"precio\":15000,\"descripcion\":\"Maíz dulce\"}";
		mvc.perform(MockMvcRequestBuilders.patch("/api/v1/platos/" + dishId)
				.header("Authorization", token(8L, "PROPIETARIO")).contentType(MediaType.APPLICATION_JSON).content(update))
				.andExpect(status().isOk()).andExpect(jsonPath("$.nombre").value("Arepa"))
				.andExpect(jsonPath("$.precio").value(15000)).andExpect(jsonPath("$.descripcion").value("Maíz dulce"));

		mvc.perform(MockMvcRequestBuilders.patch("/api/v1/platos/" + dishId)
				.header("Authorization", token(9L, "PROPIETARIO")).contentType(MediaType.APPLICATION_JSON).content(update))
				.andExpect(status().isForbidden());
		mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/v1/platos/" + dishId + "/estado")
				.header("Authorization", token(8L, "PROPIETARIO")).contentType(MediaType.APPLICATION_JSON)
				.content("{\"activo\":false}"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.activo").value(false));

		mvc.perform(MockMvcRequestBuilders.patch("/api/v1/platos/" + dishId)
				.header("Authorization", token(8L, "PROPIETARIO")).contentType(MediaType.APPLICATION_JSON)
				.content("{\"precio\":15000,\"descripcion\":\"Otra\",\"nombre\":\"Cambiar identidad\"}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void contratoInternoSoloConfirmaElRestauranteDelPropietarioAutenticado() throws Exception {
		mvc.perform(get("/api/v1/internal/restaurantes/" + ownerRestaurantId + "/propietarios/8")
				.header("Authorization", token(8L, "PROPIETARIO"))).andExpect(status().isNoContent());
		mvc.perform(get("/api/v1/internal/restaurantes/" + ownerRestaurantId + "/propietarios/9")
				.header("Authorization", token(8L, "PROPIETARIO"))).andExpect(status().isForbidden());
	}

	@Test
	void documentaListadoDeRestaurantesEnOpenApi() throws Exception {
		mvc.perform(get("/v3/api-docs")).andExpect(status().isOk())
				.andExpect(jsonPath("$.paths['/api/v1/restaurantes'].get.summary").value("Listar restaurantes"));
	}

	private String token(Long id, String role) {
		Instant now = Instant.now();
		String jwt = Jwts.builder().subject(id + "@example.test").claim("uid", id).claim("rol", role)
				.claim("restauranteId", (Object) null).issuedAt(Date.from(now)).expiration(Date.from(now.plusSeconds(3600)))
				.signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8))).compact();
		return "Bearer " + jwt;
	}
}
