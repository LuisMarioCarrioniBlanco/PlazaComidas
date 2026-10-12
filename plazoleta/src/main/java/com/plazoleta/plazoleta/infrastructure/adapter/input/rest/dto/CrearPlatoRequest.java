package com.plazoleta.plazoleta.infrastructure.adapter.input.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CrearPlatoRequest(
		@NotBlank @Size(max = 200) String nombre,
		@NotNull @Positive Integer precio,
		@NotBlank @Size(max = 2000) String descripcion,
		@NotBlank @Size(max = 2048) String urlImagen,
		@NotBlank @Size(max = 100) String categoria) {
}
