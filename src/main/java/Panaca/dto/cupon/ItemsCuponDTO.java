package Panaca.dto.cupon;

import Panaca.model.enums.EstadoCupon;
import Panaca.model.enums.TipoCupon;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ItemsCuponDTO (
        String nombre,
        LocalDateTime fechaVencimiento,
        LocalDateTime fechaApertura,
        Float descuento,
        TipoCupon tipo,
        EstadoCupon estado
){}