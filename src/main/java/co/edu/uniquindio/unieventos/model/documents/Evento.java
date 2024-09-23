package co.edu.uniquindio.unieventos.model.documents;

import co.edu.uniquindio.unieventos.model.enums.EstadoEvento;
import co.edu.uniquindio.unieventos.model.enums.TipoEvento;
import co.edu.uniquindio.unieventos.model.vo.Localidad;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document("eventos")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Evento {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    private String nombre;
    private String descripcion;
    private String direccion;
    private String ciudad;
    private LocalDateTime fecha;
    private String imagenesLocalidades;
    private String imagenPortada;
    private List<Localidad> localidades;
    private EstadoEvento estado;
    private TipoEvento tipo;
}
