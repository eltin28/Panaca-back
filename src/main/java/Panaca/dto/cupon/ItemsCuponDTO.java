package Panaca.dto.cupon;

import Panaca.model.enums.EstadoCupon;
import Panaca.model.enums.TipoCupon;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record ItemsCuponDTO (
        @NotBlank(message = "El nombre no puede estar vacío.")
        String nombre,

        //@FutureOrPresent(message = "La fecha de vencimiento debe ser en el futuro o presente.")
        LocalDate fechaVencimiento,

        //@Future(message = "La fecha de apertura debe ser en el futuro.")
        LocalDate fechaApertura,

        @NotNull(message = "El descuento no puede ser nulo.")
        @DecimalMin(value = "0.0", inclusive = false, message = "El descuento debe ser mayor que 0.")
        Float descuento,

        @NotNull(message = "El tipo de cupón no puede ser nulo.")
        TipoCupon tipo,

        @NotNull(message = "El estado del cupón no puede ser nulo.")
        EstadoCupon estado

){
}