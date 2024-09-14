package co.edu.uniquindio.unieventos.dto.cupon;

import co.edu.uniquindio.unieventos.model.enums.EstadoCupon;
import co.edu.uniquindio.unieventos.model.enums.TipoCupon;
import java.time.LocalDateTime;

public record CrearCuponDTO (
    String codigo,
    String nombre,
    float porcentajeDescuento,
    LocalDateTime fechaVencimiento,
    LocalDateTime fechaApertura,
    TipoCupon tipoCupon,
    EstadoCupon estadoCupon
){
}