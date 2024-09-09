package co.edu.uniquindio.unieventos.model.vo;

import lombok.*;
import org.springframework.data.annotation.Id;


@NoArgsConstructor
@Getter
@Setter
@ToString

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Usuario {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    private String nombre;
    private String cedula;
    private String telefono;
    private String direccion;

    @Builder

    public Usuario(String nombre, String cedula, String telefono, String direccion) {
        this.nombre = nombre;
        this.cedula = cedula;
        this.telefono = telefono;
        this.direccion = direccion;
    }
}
