package co.edu.uniquindio.unieventos.dto.email;

public record EmailDTO(
        String asunto,
        String cuerpo,
        String destinatario
) {
}
