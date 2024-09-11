package co.edu.uniquindio.unieventos.dto.cuenta;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record LoginDTO(
        @NotBlank(message = "El email es requerido")
        @Email(message = "El email no es válido")
        String email,

        @NotBlank(message = "La contraseña es requerida")
        String password
) {
}
