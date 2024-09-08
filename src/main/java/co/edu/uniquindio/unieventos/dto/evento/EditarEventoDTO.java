package co.edu.uniquindio.unieventos.dto.evento;

import co.edu.uniquindio.unieventos.model.enums.EstadoEvento;
import co.edu.uniquindio.unieventos.model.enums.TipoEvento;
import co.edu.uniquindio.unieventos.model.vo.Localidad;

import java.time.LocalDateTime;
import java.util.List;

public record EditarEventoDTO(
        String imagenPortada,
        String nombre,
        String descripcion,
        String direccion,
        String imagenesLocalidades,
        TipoEvento tipoEvento,
        EstadoEvento estadoEvento,
        LocalDateTime fecha,
        String ciudad,
        List<Localidad> listaLocalidades
) {
}
