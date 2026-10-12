package com.plazoleta.plazoleta.infrastructure.adapter.output.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "platos")
public class PlatoJpaEntity {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
	@Column(nullable = false, length = 200) private String nombre;
	@Column(nullable = false) private int precio;
	@Column(nullable = false, length = 2000) private String descripcion;
	@Column(nullable = false, length = 2048) private String urlImagen;
	@Column(nullable = false, length = 100) private String categoria;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "restaurante_id", nullable = false) private RestauranteJpaEntity restaurante;
	@Column(nullable = false) private boolean activo;

	protected PlatoJpaEntity() {}
	public PlatoJpaEntity(Long id, String nombre, int precio, String descripcion, String urlImagen, String categoria,
			RestauranteJpaEntity restaurante, boolean activo) {
		this.id = id; this.nombre = nombre; this.precio = precio; this.descripcion = descripcion;
		this.urlImagen = urlImagen; this.categoria = categoria; this.restaurante = restaurante; this.activo = activo;
	}
	public Long getId() { return id; }
	public String getNombre() { return nombre; }
	public int getPrecio() { return precio; }
	public String getDescripcion() { return descripcion; }
	public String getUrlImagen() { return urlImagen; }
	public String getCategoria() { return categoria; }
	public RestauranteJpaEntity getRestaurante() { return restaurante; }
	public boolean isActivo() { return activo; }
}
