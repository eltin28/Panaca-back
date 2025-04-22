package Panaca.dto.evento;

import Panaca.model.enums.TipoEvento;
import Panaca.model.vo.Localidad;

import java.time.LocalDate;
import java.util.List;

public record EventoFiltradoDTO(
        String urlImagenPortada,
        String nombre,
        String direccion,
        String ciudad,
        LocalDate fecha,
        TipoEvento tipoEvento,
        List<Localidad> listaLocalidades
) {
}
