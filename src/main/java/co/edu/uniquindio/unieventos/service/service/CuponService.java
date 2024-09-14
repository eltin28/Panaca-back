package co.edu.uniquindio.unieventos.service.service;

import co.edu.uniquindio.unieventos.dto.cupon.*;
import co.edu.uniquindio.unieventos.exceptions.CuponException;
import org.springframework.stereotype.Service;

@Service
public interface CuponService {

    String crearCupon(CrearCuponDTO cupon) throws CuponException;

    String editarCupon(EditarCuponDTO cupon, String cuponId) throws CuponException;

    String eliminarCupon(String id) throws CuponException;

    InformacionCuponDTO obtenerInformacionCupon(String id) throws CuponException;

}