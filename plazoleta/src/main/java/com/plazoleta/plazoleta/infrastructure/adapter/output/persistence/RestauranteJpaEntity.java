package com.plazoleta.plazoleta.infrastructure.adapter.output.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "restaurantes")
public class RestauranteJpaEntity {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
	@Column(nullable = false, length = 200) private String nombre;
	@Column(nullable = false, unique = true, length = 30) private String nit;
	@Column(nullable = false, length = 300) private String direccion;
	@Column(nullable = false, length = 13) private String telefono;
	@Column(nullable = false, length = 2048) private String urlLogo;
	@Column(nullable = false) private Long propietarioId;

	protected RestauranteJpaEntity() {}
	public RestauranteJpaEntity(Long id, String nombre, String nit, String direccion, String telefono, String urlLogo,
			Long propietarioId) {
		this.id = id; this.nombre = nombre; this.nit = nit; this.direccion = direccion; this.telefono = telefono;
		this.urlLogo = urlLogo; this.propietarioId = propietarioId;
	}
	public Long getId() { return id; }
	public String getNombre() { return nombre; }
	public String getNit() { return nit; }
	public String getDireccion() { return direccion; }
	public String getTelefono() { return telefono; }
	public String getUrlLogo() { return urlLogo; }
	public Long getPropietarioId() { return propietarioId; }
}
