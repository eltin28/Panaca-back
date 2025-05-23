package Panaca.dto.cupon;

import Panaca.model.enums.EstadoCupon;
import Panaca.model.enums.TipoCupon;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

public record EditarCuponDTO(
        @NotBlank(message = "El código del cupón no puede estar vacío.")
        @Length(min = 3, max = 30, message = "El código debe tener entre 3 y 30 caracteres.")
        String codigo,

        @NotBlank(message = "El nombre del cupón no puede estar vacío.")
        @Length(min = 5, max = 50, message = "El nombre debe tener entre 5 y 50 caracteres.")
        String nombre,

        @NotNull(message = "El porcentaje de descuento es obligatorio.")
        @DecimalMin(value = "0.0", inclusive = false, message = "El porcentaje debe ser mayor que 0.")
        @DecimalMax(value = "100.0", inclusive = true, message = "El porcentaje no puede ser mayor que 100.")
        Float porcentajeDescuento,

        @NotNull(message = "La fecha de vencimiento es obligatoria.")
        @FutureOrPresent(message = "La fecha de vencimiento debe ser presente o futura.")
        LocalDateTime fechaVencimiento,

        @FutureOrPresent(message = "La fecha de apertura debe ser presente o futura.")
        LocalDateTime fechaApertura,

        @NotNull(message = "El tipo de cupón es obligatorio.")
        TipoCupon tipoCupon,

        @NotNull(message = "El estado del cupón es obligatorio.")
        EstadoCupon estadoCupon
) {}
