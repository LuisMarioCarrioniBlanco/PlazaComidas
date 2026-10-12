package com.plazoleta.usuarios.infrastructure.adapter.output.persistence;

import com.plazoleta.usuarios.application.port.out.UsuarioRepositoryPort;
import com.plazoleta.usuarios.domain.model.Usuario;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class UsuarioPersistenceAdapter implements UsuarioRepositoryPort {
	private final SpringDataUsuarioRepository repository;

	public UsuarioPersistenceAdapter(SpringDataUsuarioRepository repository) {
		this.repository = repository;
	}

	@Override
	public Usuario guardar(Usuario usuario) {
		var entity = new UsuarioJpaEntity(usuario.id(), usuario.nombre(), usuario.apellido(), usuario.documento(),
				usuario.celular(), usuario.fechaNacimiento(), usuario.correo(), usuario.claveHash(), usuario.rol(),
				usuario.restauranteId());
		return toDomain(repository.save(entity));
	}

	@Override
	public boolean existePorCorreo(String correo) {
		return repository.existsByCorreo(correo);
	}

	@Override
	public boolean existePorDocumento(String documento) {
		return repository.existsByDocumento(documento);
	}

	@Override
	public Optional<Usuario> buscarPorCorreo(String correo) {
		return repository.findByCorreo(correo).map(UsuarioPersistenceAdapter::toDomain);
	}

	@Override
	public Optional<Usuario> buscarPorId(Long id) {
		return repository.findById(id).map(UsuarioPersistenceAdapter::toDomain);
	}

	private static Usuario toDomain(UsuarioJpaEntity entity) {
		return new Usuario(entity.getId(), entity.getNombre(), entity.getApellido(), entity.getDocumento(),
				entity.getCelular(), entity.getFechaNacimiento(), entity.getCorreo(), entity.getClaveHash(),
				entity.getRol(), entity.getRestauranteId());
	}
}
