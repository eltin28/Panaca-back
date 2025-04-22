package Panaca.dto.PQR;

import java.time.LocalDateTime;

public record ResponderPQRDTO(
        String id,
        String respuesta,
        LocalDateTime fechaRespuesta
) {}
