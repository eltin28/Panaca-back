package Panaca.test;

import Panaca.dto.carrito.CrearCarritoDTO;
import Panaca.model.documents.Carrito;
import Panaca.repository.CarritoRepository;
import Panaca.service.service.CarritoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CarritoServiceTest {

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private CarritoRepository carritoRepository;

    @Test
    public void crearCarrito_exitosamente() {
        String idUsuario = UUID.randomUUID().toString();

        CrearCarritoDTO dto = new CrearCarritoDTO(idUsuario, List.of());

        assertDoesNotThrow(() -> carritoService.crearCarrito(dto));

        Optional<Carrito> carritoOpt = carritoRepository.findByIdUsuario(idUsuario);
        assertTrue(carritoOpt.isPresent());

        Carrito carrito = carritoOpt.get();
        assertEquals(idUsuario, carrito.getIdUsuario());
        assertTrue(carrito.getItems().isEmpty());
    }

}
