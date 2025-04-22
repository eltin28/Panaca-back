package Panaca.service.service;

import Panaca.dto.carrito.CrearCarritoDTO;
import Panaca.dto.carrito.DetalleCarritoDTO;
import Panaca.dto.carrito.InformacionEventoCarritoDTO;
import Panaca.model.documents.Carrito;
import Panaca.exceptions.CarritoException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CarritoService {

    void crearCarrito(CrearCarritoDTO carritoDTO) throws CarritoException;

    public Carrito obtenerCarritoPorUsuario(String idUsuario) throws CarritoException;

    Carrito agregarItemsAlCarrito(String idUsuario, List<DetalleCarritoDTO> nuevosItemsDTO) throws CarritoException;

    Carrito eliminarItemDelCarrito(String idUsuario, String nombreLocalidad) throws CarritoException;

    Carrito vaciarCarrito(String idUsuario) throws CarritoException;

    public List<InformacionEventoCarritoDTO> listarProductosEnCarrito(String idUsuario) throws CarritoException;

    double calcularTotalCarrito(String idUsuario) throws CarritoException;

    boolean validarDisponibilidadEntradas(String idCarrito) throws CarritoException;

}
