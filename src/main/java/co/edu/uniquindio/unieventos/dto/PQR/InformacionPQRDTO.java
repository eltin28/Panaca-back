package co.edu.uniquindio.unieventos.dto.PQR;

import co.edu.uniquindio.unieventos.model.enums.CategoriaPQR;
import co.edu.uniquindio.unieventos.model.enums.EstadoPQR;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

public record InformacionPQRDTO(
        String id,
        String nombreUsuario,
        String telefonoUsuario,
        String emailUsuario,
        String direccionUsuario,
        LocalDateTime fechaCreacion,
        EstadoPQR estadoPQR,
        CategoriaPQR categoriaPQR,
        String descripcion,
        String respuesta,
        LocalDateTime fechaRespuesta
) {
}