package com.plazoleta.plazoleta.infrastructure.security;

public record UsuarioPrincipal(Long id, String correo, String rol, Long restauranteId) {
}
