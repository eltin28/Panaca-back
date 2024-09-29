package co.edu.uniquindio.unieventos.dto.evento;

import co.edu.uniquindio.unieventos.model.enums.TipoEvento;

import java.time.LocalDateTime;

public record FiltroEventoDTO(
        String id,
        String nombre,
        TipoEvento tipoEvento,
        String ciudad,
        LocalDateTime fecha
) {
}
