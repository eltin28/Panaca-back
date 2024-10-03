package co.edu.uniquindio.unieventos.dto.evento;

import co.edu.uniquindio.unieventos.model.enums.TipoEvento;
import co.edu.uniquindio.unieventos.model.vo.Localidad;

import java.time.LocalDateTime;
import java.util.List;

public record EventoFiltradoDTO(
        String urlImagenPortada,
        String nombre,
        String direccion,
        String ciudad,
        LocalDateTime fecha,
        TipoEvento tipoEvento,
        List<Localidad> listaLocalidades
) {
}
