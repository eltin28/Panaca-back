package co.edu.uniquindio.unieventos.dto.cupon;

import co.edu.uniquindio.unieventos.model.enums.EstadoCupon;
import co.edu.uniquindio.unieventos.model.enums.TipoCupon;

import java.time.LocalDateTime;

public record InformacionCuponDTO(
        String nombre,
        String codigo,
        LocalDateTime fechaVencimiento,
        float descuento,
        TipoCupon tipo,
        EstadoCupon estado
) {
}
