package Panaca.ServiceTest;

import Panaca.dto.cuenta.InformacionCuentaDTO;
import Panaca.dto.orden.CrearOrdenDTO;
import Panaca.dto.orden.DetalleOrdenDTO;
import Panaca.dto.orden.MostrarOrdenDTO;
import Panaca.dto.orden.MostrarDetalleOrdenDTO;
import Panaca.model.documents.Carrito;
import Panaca.model.documents.Evento;
import Panaca.model.documents.Orden;
import Panaca.model.enums.TipoEvento;
import Panaca.model.vo.DetalleCarrito;
import Panaca.model.vo.DetalleOrden;
import Panaca.repository.CarritoRepository;
import Panaca.repository.OrdenRepository;
import Panaca.service.service.CuentaService;
import Panaca.service.service.EventoService;
import Panaca.service.service.OrdenService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
public class OrdenServiceTest {

    @Autowired
    private OrdenService ordenService;

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private OrdenRepository ordenRepository;

    @MockBean
    private EventoService eventoService;

    @MockBean
    private CuentaService cuentaService;

    @Test
    public void testCrearOrdenDesdeCarrito() throws Exception {
        String idCliente = new ObjectId().toHexString();
        String codigoPasarela = "pasarela123";

        Evento evento = new Evento();
        evento.setId("66a2c476991cff088eb80aaf");
        evento.setPrecio(100f);
        evento.setNombre("Show Animal");
        evento.setTipo(TipoEvento.ADULTO);
        evento.setImagenPortada("url");

        when(eventoService.obtenerInformacionEvento(evento.getId())).thenReturn(evento);

        // Crear carrito con un solo item
        Carrito carrito = new Carrito();
        carrito.setIdUsuario(idCliente);
        List<DetalleCarrito> detalles = List.of(
                new DetalleCarrito(evento.getId(), 2, LocalDate.now())
        );
        carrito.setItems(detalles);
        carritoRepository.save(carrito);

        ordenService.crearOrdenDesdeCarrito(idCliente, null, codigoPasarela);

        List<Orden> ordenes = ordenRepository.findByIdCliente(idCliente);
        assertFalse(ordenes.isEmpty());
        assertEquals(2, ordenes.get(0).getDetalle().get(0).getCantidad());
    }

    @Test
    public void testCrearOrden() throws Exception {
        String idCliente = new ObjectId().toHexString();
        String eventoId = new ObjectId().toHexString();

        DetalleOrdenDTO detalle = new DetalleOrdenDTO(
                eventoId, 2, LocalDate.now()
        );

        CrearOrdenDTO ordenDTO = new CrearOrdenDTO(
                idCliente, null, "pasarela", LocalDate.now(), List.of(detalle)
        );

        ordenService.crearOrden(ordenDTO, 200.0);

        List<Orden> ordenes = ordenRepository.findByIdCliente(idCliente);
        assertFalse(ordenes.isEmpty());
        assertEquals(200.0, ordenes.get(0).getTotal());
    }

    @Test
    public void testMostrarOrden() throws Exception {
        String idOrden = new ObjectId().toHexString();
        String idCliente = new ObjectId().toHexString();
        String idEvento = new ObjectId().toHexString();

        // Simula orden con un detalle
        Orden orden = new Orden();
        orden.setId(idOrden);
        orden.setIdCliente(new ObjectId(idCliente));
        orden.setFecha(LocalDate.now());
        orden.setCodigoCupon("BIENVENIDO15");
        orden.setTotal(200.0);
        orden.setDetalle(List.of(
                new DetalleOrden(new ObjectId(idEvento), 2, LocalDate.now())
        ));
        ordenRepository.save(orden);

        // Mock evento asociado
        Evento evento = new Evento();
        evento.setId(idEvento);
        evento.setNombre("Concierto Acústico");
        evento.setTipo(TipoEvento.ADULTO);
        evento.setPrecio(100f);
        evento.setImagenPortada("url");

        // Mock cuenta asociada
        when(eventoService.obtenerInformacionEvento(idEvento)).thenReturn(evento);
        when(cuentaService.obtenerInformacionCuenta(idCliente))
                .thenReturn(new InformacionCuentaDTO(idCliente, "123", "Juan", "999", "juan@mail.com"));

        // Ejecutar lógica
        MostrarOrdenDTO dto = ordenService.mostrarOrden(idOrden);

        // Verificación general
        assertEquals("Juan", dto.nombreUsuario());
        assertEquals("BIENVENIDO15", dto.cupon());
        assertEquals(200.0, dto.total());
        assertEquals(1, dto.detalles().size());

        // Verificación del detalle
        MostrarDetalleOrdenDTO detalle = dto.detalles().get(0);
        assertEquals("Concierto Acústico", detalle.nombreEvento());
        assertEquals(100f, detalle.precio());
        assertEquals(2, detalle.cantidad());
        assertEquals(TipoEvento.ADULTO, detalle.tipoEvento());
    }

    @Test
    public void testEliminarOrden() throws Exception {
        String idOrden = new ObjectId().toHexString();
        Orden orden = new Orden();
        orden.setId(idOrden);
        orden.setIdCliente(new ObjectId());
        orden.setDetalle(List.of(
                new DetalleOrden(new ObjectId(), 1, LocalDate.now())
        ));
        ordenRepository.save(orden);

        ordenService.eliminarOrden(idOrden);

        assertTrue(ordenRepository.findById(idOrden).isEmpty());
    }
}