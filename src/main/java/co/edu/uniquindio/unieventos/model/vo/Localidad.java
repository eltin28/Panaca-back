package co.edu.uniquindio.unieventos.model.vo;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Localidad {

    private String nombre;
    private int entradasVendidas;
    private int capacidadMaxima;
    private double precio;
}

