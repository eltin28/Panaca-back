package Panaca.dto.carrito;

import java.time.LocalDate;

public record InformacionEventoCarritoDTO(
        DetalleCarritoDTO detalleCarritoDTO,
        String imagenPortada,
        String nombre,
        String direccion,
        LocalDate fecha
) {}