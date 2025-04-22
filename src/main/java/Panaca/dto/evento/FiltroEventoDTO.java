package Panaca.dto.evento;

import Panaca.model.enums.TipoEvento;

import java.time.LocalDate;

public record FiltroEventoDTO(
        String nombre,
        TipoEvento tipo,
        String ciudad,
        LocalDate fecha
) {
}