package Panaca.dto.PQR;

import Panaca.model.enums.CategoriaPQR;

public record CrearPQRDTO(
        String idUsuario,
        CategoriaPQR categoriaPQR,
        String descripcion

) {}
