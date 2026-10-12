package com.plazoleta.plazoleta.infrastructure.adapter.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataPlatoRepository extends JpaRepository<PlatoJpaEntity, Long> {
}
