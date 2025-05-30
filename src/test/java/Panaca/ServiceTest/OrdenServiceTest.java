package Panaca.ServiceTest;

import Panaca.dto.cuenta.InformacionCuentaDTO;
import Panaca.dto.orden.CrearOrdenDTO;
import Panaca.dto.orden.DetalleOrdenDTO;
import Panaca.dto.orden.MostrarOrdenDTO;
import Panaca.exceptions.CarritoException;
import Panaca.exceptions.CuentaException;
import Panaca.exceptions.CuponException;
import Panaca.exceptions.OrdenException;
import Panaca.model.documents.Carrito;
import Panaca.model.documents.Evento;
import Panaca.model.documents.Orden;
import Panaca.model.enums.TipoEvento;
import Panaca.model.vo.DetalleCarrito;
import Panaca.model.vo.DetalleOrden;
import Panaca.repository.CarritoRepository;
import Panaca.repository.CuponRepository;
import Panaca.repository.OrdenRepository;
import Panaca.service.service.CuentaService;
import Panaca.service.service.EventoService;
import Panaca.service.service.OrdenService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
public class OrdenServiceTest {

    @Autowired
    private OrdenService ordenService;

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private CuponRepository cuponRepository;

    @Autowired
    private OrdenRepository ordenRepository;

    @Autowired
    private EventoService eventoService;

    @Autowired
    private CuentaService cuentaService;

    @Test
    public void testCrearOrdenDesdeCarrito() throws OrdenException, CarritoException, CuponException {
        // Datos de prueba
        String idCliente = "cliente123";
        String codigoCupon = "cupon123";
        String codigoPasarela = "pasarela123";

        // Crear un carrito para el cliente
        Carrito carrito = new Carrito();
        carrito.setIdUsuario(idCliente);
        List<DetalleCarrito> detalles = new ArrayList<>();
        detalles.add(new DetalleCarrito("66a2c476991cff088eb80aaf", "VIP", 2, 100.0f, LocalDate.now()));
        carrito.setItems(detalles);
        carritoRepository.save(carrito);

        // Llamada al método que queremos probar
        ordenService.crearOrdenDesdeCarrito(idCliente, codigoCupon, codigoPasarela);

        // Verificar que la orden fue creada
        List<Orden> ordenes = ordenRepository.findByIdCliente(idCliente);
        assertFalse(ordenes.isEmpty(), "La orden debería haber sido creada");
        Orden ordenCreada = ordenes.get(0);
        assertEquals(2, ordenCreada.getDetalle().get(0).getCantidad());
        assertEquals(100.0, ordenCreada.getDetalle().get(0).getPrecio());
    }


    @Test
    public void testCrearOrden() throws OrdenException {
        // Datos de prueba
        String idCliente = "cliente123";
        String codigoCupon = "cupon123";
        String codigoPasarela = "pasarela123";

        List<DetalleOrdenDTO> detallesOrdenDTO = new ArrayList<>();
        detallesOrdenDTO.add(new DetalleOrdenDTO("66a2c476991cff088eb80aaf", "VIP", 100, 2));

        CrearOrdenDTO crearOrdenDTO = new CrearOrdenDTO(idCliente, codigoCupon, codigoPasarela, LocalDate.now(), detallesOrdenDTO);

        // Llamar al método que estamos probando
        ordenService.crearOrden(crearOrdenDTO, 200.0);

        // Verificar que la orden fue creada correctamente
        List<Orden> ordenes = ordenRepository.findByIdCliente(String.valueOf(new ObjectId(idCliente)));
        assertFalse(ordenes.isEmpty(), "La orden debería haber sido creada");
        Orden ordenCreada = ordenes.get(0);
        assertEquals(200.0, ordenCreada.getTotal());
    }

    @Test
    public void testMostrarOrden() throws OrdenException, CuentaException {
        // Datos de prueba
        String idOrden = "orden123";
        String idCliente = "cliente123";

        // Crear una orden de prueba en la base de datos
        Orden orden = new Orden();
        orden.setId(idOrden);
        orden.setIdCliente(idCliente);
        List<DetalleOrden> detalles = new ArrayList<>();
        detalles.add(new DetalleOrden("evento123", "Localidad VIP", 1, 200.0));
        orden.setDetalle(detalles);
        orden.setFecha(LocalDate.now());
        ordenRepository.save(orden);

        // Crear cuenta de prueba
        InformacionCuentaDTO cuenta = new InformacionCuentaDTO(idCliente, "1234567890", "Juan Perez", "0987654321", "Direccion", "juan@example.com");

        // Stub para obtener la información del usuario y del evento
        when(cuentaService.obtenerInformacionCuenta(anyString())).thenReturn(cuenta);
        when(eventoService.obtenerInformacionEvento(anyString())).thenReturn(new Evento("evento123", "Concierto", TipoEvento.CONCIERTO, "Ciudad X", LocalDate.now()));

        // Llamar al método que estamos probando
        MostrarOrdenDTO resultado = ordenService.mostrarOrden(idOrden);

        // Verificar el resultado
        assertNotNull(resultado);
        assertEquals("Juan Perez", resultado.nombreUsuario());
        assertEquals("Concierto", resultado.nombreEvento());
        assertEquals(100.0, resultado.precio());
    }


    @Test
    public void testEliminarOrden() throws OrdenException {
        // Datos de prueba
        String idOrden = "orden123";
        String idCliente = "cliente123";

        // Crear una orden de prueba en la base de datos
        Orden orden = new Orden();
        orden.setId(idOrden);
        orden.setIdCliente(idCliente);
        List<DetalleOrden> detalles = new ArrayList<>();
        detalles.add(new DetalleOrden("100.0", "Localidad VIP", 12, 100.0));
        orden.setDetalle(detalles);
        ordenRepository.save(orden);

        // Llamar al método de eliminación
        ordenService.eliminarOrden(idOrden);

        // Verificar que la orden fue eliminada
        Optional<Orden> ordenEliminada = ordenRepository.findById(idOrden);
        assertTrue(ordenEliminada.isEmpty(), "La orden debería haber sido eliminada");
    }




}





