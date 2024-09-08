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
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DetalleOrden {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    private ObjectId idEvento;
    private float precio;
    private String nombreLocalidad;
    private int cantidad;


}
