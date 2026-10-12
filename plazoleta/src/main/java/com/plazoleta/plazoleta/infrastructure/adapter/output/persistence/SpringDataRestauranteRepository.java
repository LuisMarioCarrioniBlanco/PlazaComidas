package com.plazoleta.plazoleta.infrastructure.adapter.output.persistence;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataRestauranteRepository extends JpaRepository<RestauranteJpaEntity, Long> {
	boolean existsByNit(String nit);
	Optional<RestauranteJpaEntity> findByIdAndPropietarioId(Long id, Long propietarioId);
	default Page<RestauranteJpaEntity> paginaPorNombre(int pagina, int tamano) {
		return findAll(PageRequest.of(pagina, tamano, Sort.by(Sort.Direction.ASC, "nombre")));
	}
}
