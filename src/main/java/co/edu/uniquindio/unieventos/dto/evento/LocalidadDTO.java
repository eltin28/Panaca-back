package co.edu.uniquindio.unieventos.dto.evento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record LocalidadDTO(
        @NotBlank(message = "El nombre no puede estar vacio")
        String nombre,
        @NotNull(message = "Debes ingresar la capacidad de la localidad")
        int capacidadMaxima,
        @NotNull(message = "Debes agregar un precio")
        double precio
) {
}
