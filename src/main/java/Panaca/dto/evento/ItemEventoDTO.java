package Panaca.dto.evento;

import Panaca.model.enums.TipoEvento;

import java.time.LocalDate;

public record ItemEventoDTO(
        String id,
        String urlImagenPortada,
        String nombre,
        TipoEvento tipoEvento,
        LocalDate fecha,
        String direccion,
        String ciudad
) {
}
