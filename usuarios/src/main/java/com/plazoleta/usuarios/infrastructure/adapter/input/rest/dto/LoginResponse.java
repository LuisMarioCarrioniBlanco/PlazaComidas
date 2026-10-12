package com.plazoleta.usuarios.infrastructure.adapter.input.rest.dto;

import com.plazoleta.usuarios.domain.model.Rol;

public record LoginResponse(String accessToken, String tokenType, long expiresInSeconds, Long usuarioId, Rol rol) {
}
