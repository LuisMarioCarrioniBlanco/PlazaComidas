package com.plazoleta.plazoleta.infrastructure.adapter.output.persistence;

import com.plazoleta.plazoleta.application.port.out.RestauranteRepositoryPort;
import com.plazoleta.plazoleta.domain.model.Restaurante;
import com.plazoleta.plazoleta.domain.model.ResultadoPaginado;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class RestaurantePersistenceAdapter implements RestauranteRepositoryPort {
	private final SpringDataRestauranteRepository repository;
	public RestaurantePersistenceAdapter(SpringDataRestauranteRepository repository) { this.repository = repository; }

	@Override public Restaurante guardar(Restaurante restaurant) {
		var entity = new RestauranteJpaEntity(restaurant.id(), restaurant.nombre(), restaurant.nit(), restaurant.direccion(),
				restaurant.telefono(), restaurant.urlLogo(), restaurant.propietarioId());
		return toDomain(repository.save(entity));
	}
	@Override public boolean existePorNit(String nit) { return repository.existsByNit(nit); }
	@Override public Optional<Restaurante> buscarPorId(Long id) { return repository.findById(id).map(RestaurantePersistenceAdapter::toDomain); }
	@Override public ResultadoPaginado<Restaurante> listarOrdenados(int pagina, int tamano) {
		var result = repository.paginaPorNombre(pagina, tamano);
		return new ResultadoPaginado<>(result.getContent().stream().map(RestaurantePersistenceAdapter::toDomain).toList(),
				pagina, tamano, result.getTotalElements(), result.getTotalPages());
	}
	private static Restaurante toDomain(RestauranteJpaEntity entity) {
		return new Restaurante(entity.getId(), entity.getNombre(), entity.getNit(), entity.getDireccion(),
				entity.getTelefono(), entity.getUrlLogo(), entity.getPropietarioId());
	}
}
