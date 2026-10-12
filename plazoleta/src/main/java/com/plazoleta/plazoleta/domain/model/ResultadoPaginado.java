package com.plazoleta.plazoleta.domain.model;

import java.util.List;

public record ResultadoPaginado<T>(List<T> contenido, int pagina, int tamano, long totalElementos,
		int totalPaginas) {
}
