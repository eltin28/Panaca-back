package Panaca.dto.devolucion;

import Panaca.model.enums.EstadoDevolucion;
import jakarta.validation.constraints.NotNull;

public record CambiarEstadoDevolucionDTO(
        @NotNull(message = "El nuevo estado es obligatorio.")
        EstadoDevolucion nuevoEstado
) {}