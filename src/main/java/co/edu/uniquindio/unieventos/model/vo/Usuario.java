package co.edu.uniquindio.unieventos.model.vo;

import lombok.*;


@NoArgsConstructor
@Getter
@Setter
@ToString
public class Usuario {

    //private String id;
    private String nombreCompleto;
    private String cedula;
    private String telefono;
    private String direccion;

    @Builder
    public Usuario(String nombreCompleto, String cedula, String telefono, String direccion) {
        this.nombreCompleto = nombreCompleto;
        this.cedula = cedula;
        this.telefono = telefono;
        this.direccion = direccion;
    }
}
