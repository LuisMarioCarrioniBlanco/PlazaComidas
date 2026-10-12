package com.plazoleta.usuarios.infrastructure.adapter.output.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataUsuarioRepository extends JpaRepository<UsuarioJpaEntity, Long> {
	boolean existsByCorreo(String correo);
	boolean existsByDocumento(String documento);
	Optional<UsuarioJpaEntity> findByCorreo(String correo);
}
