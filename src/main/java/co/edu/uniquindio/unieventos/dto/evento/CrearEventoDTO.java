package co.edu.uniquindio.unieventos.dto.evento;

import co.edu.uniquindio.unieventos.model.enums.EstadoEvento;
import co.edu.uniquindio.unieventos.model.enums.TipoEvento;
import co.edu.uniquindio.unieventos.model.vo.Localidad;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public record CrearEventoDTO(
        @NotEmpty(message = "La imagen de portada es obligatoria.")
        String imagenPortada,

        @NotEmpty(message = "El nombre del evento es obligatorio.")
        String nombre,

        @NotEmpty(message = "La descripción del evento es obligatoria.")
        String descripcion,

        @NotEmpty(message = "La dirección del evento es obligatoria.")
        String direccion,

        @NotEmpty(message = "La imagen de las localidades es obligatoria.")
        String imagenLocalidad,

        @NotNull(message = "El tipo de evento es obligatorio.")
        TipoEvento tipoEvento,

        @NotNull(message = "El estado del evento es obligatorio.")
        EstadoEvento estadoEvento,

        @NotNull(message = "La fecha del evento es obligatoria.")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime fecha,

        @NotEmpty(message = "La ciudad es obligatoria.")
        String ciudad,

        @NotNull(message = "La lista de localidades no puede ser nula.")
        List<LocalidadDTO> listaLocalidades
) {
}
