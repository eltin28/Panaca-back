package co.edu.uniquindio.unieventos.model.vo;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DetalleCarrito {

    private int cantidad;
    private String nombreLocalidad;

    @Id
    private ObjectId idEvento;
}
