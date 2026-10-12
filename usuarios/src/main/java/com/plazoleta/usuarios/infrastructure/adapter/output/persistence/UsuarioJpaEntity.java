package com.plazoleta.usuarios.infrastructure.adapter.output.persistence;

import com.plazoleta.usuarios.domain.model.Rol;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "usuarios")
public class UsuarioJpaEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false, length = 100)
	private String nombre;
	@Column(nullable = false, length = 100)
	private String apellido;
	@Column(nullable = false, unique = true, length = 30)
	private String documento;
	@Column(nullable = false, length = 13)
	private String celular;
	private LocalDate fechaNacimiento;
	@Column(nullable = false, unique = true, length = 254)
	private String correo;
	@Column(nullable = false)
	private String claveHash;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private Rol rol;
	private Long restauranteId;

	protected UsuarioJpaEntity() {
	}

	public UsuarioJpaEntity(Long id, String nombre, String apellido, String documento, String celular,
			LocalDate fechaNacimiento, String correo, String claveHash, Rol rol, Long restauranteId) {
		this.id = id;
		this.nombre = nombre;
		this.apellido = apellido;
		this.documento = documento;
		this.celular = celular;
		this.fechaNacimiento = fechaNacimiento;
		this.correo = correo;
		this.claveHash = claveHash;
		this.rol = rol;
		this.restauranteId = restauranteId;
	}

	public Long getId() { return id; }
	public String getNombre() { return nombre; }
	public String getApellido() { return apellido; }
	public String getDocumento() { return documento; }
	public String getCelular() { return celular; }
	public LocalDate getFechaNacimiento() { return fechaNacimiento; }
	public String getCorreo() { return correo; }
	public String getClaveHash() { return claveHash; }
	public Rol getRol() { return rol; }
	public Long getRestauranteId() { return restauranteId; }
}
