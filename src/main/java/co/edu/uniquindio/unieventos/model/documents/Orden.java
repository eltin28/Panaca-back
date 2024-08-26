package co.edu.uniquindio.unieventos.model.documents;

import co.edu.uniquindio.unieventos.model.vo.DetalleOrden;
import co.edu.uniquindio.unieventos.model.vo.Pago;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Document("Orden")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Orden {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    @Id
    private ObjectId idCliente;
    @Id
    private ObjectId idCupon;

    private LocalDateTime fecha;
    private String codigoPasarela;
    private Pago pago;
    private List<DetalleOrden> items;
    private float total;


}
