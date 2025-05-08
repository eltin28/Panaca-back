package Panaca.ServiceTest;

import Panaca.dto.carrito.CrearCarritoDTO;
import Panaca.dto.carrito.DetalleCarritoDTO;
import Panaca.dto.carrito.InformacionEventoCarritoDTO;
import Panaca.exceptions.CarritoException;
import Panaca.model.documents.Carrito;
import Panaca.model.documents.Evento;
import Panaca.model.enums.TipoEvento;
import Panaca.model.vo.DetalleCarrito;
import Panaca.repository.CarritoRepository;
import Panaca.repository.EventoRepository;
import Panaca.service.implement.CarritoServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mongodb.assertions.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class CarritoServiceTest {

    @Mock private CarritoRepository carritoRepository;
    @Mock private EventoRepository eventoRepository;

    @InjectMocks
    private CarritoServiceImp carritoService;

    private Carrito carrito;
    private Evento evento;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        carrito = new Carrito();
        carrito.setIdUsuario("user123");
        carrito.setItems(new ArrayList<>());

        evento = new Evento();
        evento.setId("evt123");
        evento.setNombre("Festival");
        evento.setImagenPortada("url-img.jpg");
        evento.setPrecio(50.0f);
        evento.setTipo(TipoEvento.ADULTO);
    }

    @Test
    void crearCarrito_guardadoCorrectamente() {
        CrearCarritoDTO dto = new CrearCarritoDTO("user123", new ArrayList<>());
        carritoService.crearCarrito(dto);
        verify(carritoRepository).save(any(Carrito.class));
    }

    @Test
    void agregarItemsAlCarrito_nuevoItemSeAgrega() throws CarritoException {
        DetalleCarritoDTO itemDTO = new DetalleCarritoDTO("evt123", 2, LocalDate.now());
        when(carritoRepository.findByIdUsuario("user123")).thenReturn(Optional.of(carrito));

        Carrito result = carritoService.agregarItemsAlCarrito("user123", List.of(itemDTO));

        assertEquals(1, result.getItems().size());
        assertEquals("evt123", result.getItems().get(0).getIdEvento());
        verify(carritoRepository).save(carrito);
    }

    @Test
    void eliminarItemDelCarrito_eliminaCorrectamente() throws CarritoException {
        carrito.setItems(List.of(new DetalleCarrito("evt123", 1, LocalDate.now())));
        when(carritoRepository.findByIdUsuario("user123")).thenReturn(Optional.of(carrito));

        Carrito actualizado = carritoService.eliminarItemDelCarrito("user123", "evt123", LocalDate.now());
        assertTrue(actualizado.getItems().isEmpty());
    }

    @Test
    void vaciarCarrito_borraTodosLosItems() throws CarritoException {
        carrito.setItems(List.of(
                new DetalleCarrito("evt123", 1, LocalDate.now()),
                new DetalleCarrito("evt456", 2, LocalDate.now())
        ));
        when(carritoRepository.findByIdUsuario("user123")).thenReturn(Optional.of(carrito));

        Carrito vacio = carritoService.vaciarCarrito("user123");
        assertTrue(vacio.getItems().isEmpty());
    }

    @Test
    void listarProductosEnCarrito_devuelveInformacion() throws CarritoException {
        carrito.setItems(List.of(new DetalleCarrito("evt123", 2, LocalDate.now())));
        when(carritoRepository.findByIdUsuario("user123")).thenReturn(Optional.of(carrito));
        when(eventoRepository.findById("evt123")).thenReturn(Optional.of(evento));

        List<InformacionEventoCarritoDTO> lista = carritoService.listarProductosEnCarrito("user123");
        assertEquals(1, lista.size());
        assertEquals("Festival", lista.get(0).nombre());
    }

    @Test
    void calcularTotalCarrito_sumaCorrectamente() throws CarritoException {
        carrito.setItems(List.of(new DetalleCarrito("evt123", 2, LocalDate.now())));
        when(carritoRepository.findByIdUsuario("user123")).thenReturn(Optional.of(carrito));
        when(eventoRepository.findById("evt123")).thenReturn(Optional.of(evento));

        double total = carritoService.calcularTotalCarrito("user123");
        assertEquals(100.0, total);
    }
}
