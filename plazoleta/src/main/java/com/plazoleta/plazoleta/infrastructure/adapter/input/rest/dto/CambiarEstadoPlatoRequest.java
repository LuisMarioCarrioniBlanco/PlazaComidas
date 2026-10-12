package com.plazoleta.plazoleta.infrastructure.adapter.input.rest.dto;

import jakarta.validation.constraints.NotNull;

public record CambiarEstadoPlatoRequest(@NotNull Boolean activo) {
}
