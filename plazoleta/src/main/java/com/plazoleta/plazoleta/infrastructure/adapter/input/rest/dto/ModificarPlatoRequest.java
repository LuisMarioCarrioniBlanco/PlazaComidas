package com.plazoleta.plazoleta.infrastructure.adapter.input.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ModificarPlatoRequest(@NotNull @Positive Integer precio,
		@NotBlank @Size(max = 2000) String descripcion) {
}
