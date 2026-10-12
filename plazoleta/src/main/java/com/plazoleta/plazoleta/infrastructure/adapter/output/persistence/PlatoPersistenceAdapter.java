package com.plazoleta.plazoleta.infrastructure.adapter.output.persistence;

import com.plazoleta.plazoleta.application.port.out.PlatoRepositoryPort;
import com.plazoleta.plazoleta.domain.model.Plato;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class PlatoPersistenceAdapter implements PlatoRepositoryPort {
	private final SpringDataPlatoRepository platos;
	private final SpringDataRestauranteRepository restaurantes;
	public PlatoPersistenceAdapter(SpringDataPlatoRepository platos, SpringDataRestauranteRepository restaurantes) {
		this.platos = platos; this.restaurantes = restaurantes;
	}
	@Override public Plato guardar(Plato plato) {
		var restaurante = restaurantes.getReferenceById(plato.restauranteId());
		var entity = new PlatoJpaEntity(plato.id(), plato.nombre(), plato.precio(), plato.descripcion(), plato.urlImagen(),
				plato.categoria(), restaurante, plato.activo());
		return toDomain(platos.save(entity));
	}
	@Override public Optional<Plato> buscarPorId(Long id) { return platos.findById(id).map(PlatoPersistenceAdapter::toDomain); }
	private static Plato toDomain(PlatoJpaEntity entity) {
		return new Plato(entity.getId(), entity.getNombre(), entity.getPrecio(), entity.getDescripcion(), entity.getUrlImagen(),
				entity.getCategoria(), entity.getRestaurante().getId(), entity.isActivo());
	}
}
