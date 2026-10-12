package com.plazoleta.usuarios.infrastructure.security;

import com.plazoleta.usuarios.domain.model.Rol;

public record UsuarioPrincipal(Long id, String correo, Rol rol, Long restauranteId) {
}
