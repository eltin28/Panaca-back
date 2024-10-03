package co.edu.uniquindio.unieventos.dto.evento;

import co.edu.uniquindio.unieventos.model.enums.TipoEvento;

import java.time.LocalDateTime;

public record ItemEventoDTO(
        String urlImagenPortada,
        String nombre,
        TipoEvento tipoEvento,
        LocalDateTime fecha,
        String direccion,
        String ciudad
) {
}
