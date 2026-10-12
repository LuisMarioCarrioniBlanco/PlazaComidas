package com.plazoleta.usuarios.infrastructure.config;

import com.plazoleta.usuarios.application.port.out.PasswordHasherPort;
import com.plazoleta.usuarios.application.port.out.UsuarioRepositoryPort;
import com.plazoleta.usuarios.domain.model.Rol;
import com.plazoleta.usuarios.domain.model.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class BootstrapAdminRunner implements ApplicationRunner {
	private final UsuarioRepositoryPort usuarios;
	private final PasswordHasherPort passwordHasher;
	private final String email;
	private final String password;
	private final String nombre;
	private final String apellido;
	private final String documento;
	private final String celular;

	public BootstrapAdminRunner(UsuarioRepositoryPort usuarios, PasswordHasherPort passwordHasher,
			@Value("${bootstrap.admin.email:}") String email,
			@Value("${bootstrap.admin.password:}") String password,
			@Value("${bootstrap.admin.name:}") String nombre,
			@Value("${bootstrap.admin.last-name:}") String apellido,
			@Value("${bootstrap.admin.document:}") String documento,
			@Value("${bootstrap.admin.phone:}") String celular) {
		this.usuarios = usuarios;
		this.passwordHasher = passwordHasher;
		this.email = email;
		this.password = password;
		this.nombre = nombre;
		this.apellido = apellido;
		this.documento = documento;
		this.celular = celular;
	}

	@Override
	public void run(ApplicationArguments args) {
		if (email.isBlank() && password.isBlank() && nombre.isBlank() && apellido.isBlank()
				&& documento.isBlank() && celular.isBlank()) {
			return;
		}
		if (email.isBlank() || password.isBlank() || nombre.isBlank() || apellido.isBlank()
				|| documento.isBlank() || celular.isBlank()) {
			throw new IllegalStateException("La configuración de inicialización del administrador está incompleta");
		}
		if (usuarios.existePorCorreo(email.trim().toLowerCase(java.util.Locale.ROOT))) {
			return;
		}
		if (usuarios.existePorDocumento(documento)) {
			throw new IllegalStateException("El documento configurado para el administrador ya está registrado");
		}
		usuarios.guardar(new Usuario(null, nombre.trim(), apellido.trim(), documento, celular, null,
				email.trim().toLowerCase(java.util.Locale.ROOT), passwordHasher.hash(password), Rol.ADMIN, null));
	}
}
