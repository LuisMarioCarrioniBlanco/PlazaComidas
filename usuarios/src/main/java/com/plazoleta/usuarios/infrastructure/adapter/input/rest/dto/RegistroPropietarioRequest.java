package com.plazoleta.usuarios.infrastructure.adapter.input.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record RegistroPropietarioRequest(
		@NotBlank @Size(max = 100) String nombre,
		@NotBlank @Size(max = 100) String apellido,
		@NotBlank @Pattern(regexp = "[0-9]+") @Size(max = 30) String documento,
		@NotBlank @Pattern(regexp = "\\+?[0-9]{1,12}") String celular,
		@NotNull @Past LocalDate fechaNacimiento,
		@NotBlank @Email @Size(max = 254) String correo,
		@NotBlank String clave) {
}
