package Panaca.model.documents;

import Panaca.model.enums.EstadoEvento;
import Panaca.model.enums.TipoEvento;
import Panaca.model.vo.Localidad;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
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
    private LocalDate fecha;
    private String imagenLocalidad;
    private String imagenPortada;
    private List<Localidad> localidades;
    private EstadoEvento estado;
    private TipoEvento tipo;

    public Evento(String id,String nombre,TipoEvento tipo, String ciudad, LocalDate fecha){
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.ciudad = ciudad;
        this.fecha = fecha;
    }

}
