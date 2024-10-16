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
    private Float precio;

    public Localidad(String nombre, int capacidadMaxima, Float precio) {
        this.nombre = nombre;
        this.capacidadMaxima = capacidadMaxima;
        this.precio = precio;
    }
}

