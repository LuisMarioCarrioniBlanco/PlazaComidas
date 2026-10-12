package com.plazoleta.plazoleta.infrastructure.adapter.input.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CrearRestauranteRequest(
		@NotBlank @Size(max = 200) @Pattern(regexp = ".*[^0-9].*") String nombre,
		@NotBlank @Pattern(regexp = "[0-9]+") @Size(max = 30) String nit,
		@NotBlank @Size(max = 300) String direccion,
		@NotBlank @Pattern(regexp = "\\+?[0-9]{1,12}") String telefono,
		@NotBlank @Size(max = 2048) String urlLogo,
		@NotNull @Positive Long propietarioId) {
}
