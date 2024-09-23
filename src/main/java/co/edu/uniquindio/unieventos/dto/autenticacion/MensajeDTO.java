package co.edu.uniquindio.unieventos.dto.autenticacion;

public record MensajeDTO<T>(
        boolean error,
        T respuesta
) {
}
