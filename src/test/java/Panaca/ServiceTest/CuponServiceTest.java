package Panaca.ServiceTest;

import Panaca.dto.cupon.CrearCuponDTO;
import Panaca.dto.cupon.EditarCuponDTO;
import Panaca.dto.cupon.InformacionCuponDTO;
import Panaca.dto.cupon.ItemsCuponDTO;
import Panaca.exceptions.CuponException;
import Panaca.model.documents.Cupon;
import Panaca.model.enums.EstadoCupon;
import Panaca.model.enums.TipoCupon;
import Panaca.repository.CuponRepository;
import Panaca.service.service.CuponService;
import Panaca.service.service.EventoService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class CuponServiceTest {

    @Autowired
    private CuponService cuponService;
    @Autowired
    private CuponRepository cuponRepository;
    @Autowired
    private EventoService eventoService;

    @Test
    public void crearCuponTest() {
        CrearCuponDTO cuponDTO = new CrearCuponDTO(
            "AMAYVIVE",
            "Amor y amistad",
            10.0f,
            LocalDateTime.now().plusDays(30),
            LocalDateTime.now(),
            TipoCupon.MULTIPLE,
            EstadoCupon.DISPONIBLE
        );
        assertDoesNotThrow(() -> cuponService.crearCupon(cuponDTO));
    }

    @Test
    public void actualizarCuponTest(){

        String idCupon = "66eb4fde26fa517dc05efb45";

        EditarCuponDTO editarCuponDTO = new EditarCuponDTO(
                "Octubre",
                "Hallowen",
                10.0f,
                LocalDateTime.now().plusDays(30),
                LocalDateTime.now(),
                TipoCupon.MULTIPLE,
                EstadoCupon.DISPONIBLE

        );

        assertDoesNotThrow(() -> {
            cuponService.editarCupon(editarCuponDTO, idCupon);
        });
    }

    @Test
    public void listarCuponesSinFiltrosTest() {
        // Probar sin filtros, debería devolver todos los cupones
        assertDoesNotThrow(() -> {
            // Se crea un DTO vacío para no aplicar filtros
            ItemsCuponDTO itemCuponDTO = new ItemsCuponDTO(
                    "AMOR Y AMISTAD", // nombre
                    LocalDate.now().plusDays(10), // fechaVencimiento
                    LocalDate.now(), // fechaApertura
                    10.0f, // descuento
                    TipoCupon.UNICO, // tipo
                    EstadoCupon.DISPONIBLE// estado
            );

            List<ItemsCuponDTO> cupones = cuponService.obtenerCuponesFiltrados(itemCuponDTO);

            // Se espera que la lista no esté vacía
            assertFalse(cupones.isEmpty());
        });
    }

    @Test
    public void testEliminarCupon() throws CuponException {
        // Crear primero el cupón para poder eliminarlo
        CrearCuponDTO crearCuponDTO = new CrearCuponDTO(
                "Cupon de Prueba",
                "CUPON123",
                100.0f,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(10),
                TipoCupon.MULTIPLE,
                EstadoCupon.DISPONIBLE
        );
        cuponService.crearCupon(crearCuponDTO);

        // Obtener el cupón existente
        Cupon cuponExistente = cuponRepository.findByCodigo("CUPON123");
        assertNotNull(cuponExistente);

        // Eliminar el cupón
        cuponService.eliminarCupon(cuponExistente.getId());

        // Verificar que el cupón ya no existe
        assertFalse(cuponRepository.findById(cuponExistente.getId()).isPresent());
    }

    @Test
    public void testObtenerInformacionCupon() throws CuponException {
        CrearCuponDTO crearCuponDTO = new CrearCuponDTO(
                "Cupon de Prueba",
                "CUPON123",
                100.0f,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(10),
                TipoCupon.MULTIPLE,
                EstadoCupon.DISPONIBLE
        );
        cuponService.crearCupon(crearCuponDTO);

        Cupon cuponExistente = cuponRepository.findByCodigo("CUPON123");
        assertNotNull(cuponExistente);

        InformacionCuponDTO infoCupon = cuponService.obtenerInformacionCupon(cuponExistente.getId());

        assertEquals("Cupon de Prueba", infoCupon.nombre());
        assertEquals("CUPON123", infoCupon.codigo());
        assertEquals(20, infoCupon.porcentajeDescuento());
        assertNotNull(infoCupon.fechaApertura());
        assertNotNull(infoCupon.fechaVencimiento());
        Assertions.assertEquals("Porcentaje", infoCupon.tipoCupon());
        Assertions.assertEquals(EstadoCupon.DISPONIBLE, infoCupon.estadoCupon());
    }

}
