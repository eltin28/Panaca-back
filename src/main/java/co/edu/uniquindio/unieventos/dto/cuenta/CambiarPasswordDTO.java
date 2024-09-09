package co.edu.uniquindio.unieventos.dto.cuenta;

public record CambiarPasswordDTO(
        String codigoVerificacion,
        String passwordNueva
) {
}
