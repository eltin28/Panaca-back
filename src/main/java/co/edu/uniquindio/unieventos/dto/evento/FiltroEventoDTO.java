package co.edu.uniquindio.unieventos.dto.evento;

import co.edu.uniquindio.unieventos.model.enums.TipoEvento;

public record FiltroEventoDTO(
        String nombre,
        TipoEvento tipoEvento,
        String ciudad
) {
}
