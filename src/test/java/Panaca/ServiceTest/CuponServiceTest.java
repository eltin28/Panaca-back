package Panaca.ServiceTest;

import Panaca.dto.cupon.*;
import Panaca.exceptions.CuponException;
import Panaca.model.documents.Cupon;
import Panaca.model.enums.EstadoCupon;
import Panaca.model.enums.TipoCupon;
import Panaca.repository.CuponRepository;
import Panaca.service.service.CuponService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class CuponServiceTest {

    @Autowired private CuponService cuponService;
    @Autowired private CuponRepository cuponRepository;

    @Test
    void crearCupon_exitoso() {
        CrearCuponDTO dto = new CrearCuponDTO("Bienvenida", "WELCOME123", 10.0f,
                LocalDateTime.now().plusDays(30), LocalDateTime.now(), TipoCupon.UNICO, EstadoCupon.DISPONIBLE);

        assertDoesNotThrow(() -> cuponService.crearCupon(dto));
    }

    @Test
    void crearCupon_codigoDuplicado_lanzaExcepcion() {
        Cupon cupon = new Cupon("Nombre", "REPE123", LocalDateTime.now().plusDays(10),
                LocalDateTime.now(), 5.0f, TipoCupon.UNICO, EstadoCupon.DISPONIBLE, false);
        cuponRepository.save(cupon);

        CrearCuponDTO dto = new CrearCuponDTO("Otro", "REPE123", 10.0f,
                LocalDateTime.now().plusDays(30), LocalDateTime.now(), TipoCupon.UNICO, EstadoCupon.DISPONIBLE);

        assertThrows(CuponException.class, () -> cuponService.crearCupon(dto));
    }

    @Test
    void editarCupon_exitoso() throws CuponException {
        Cupon cupon = new Cupon("Editar", "EDIT001", LocalDateTime.now().plusDays(10),
                LocalDateTime.now(), 5.0f, TipoCupon.MULTIPLE, EstadoCupon.DISPONIBLE, false);
        cuponRepository.save(cupon);

        EditarCuponDTO dto = new EditarCuponDTO("Actualizado", "EDIT001", 15.0f,
                LocalDateTime.now().plusDays(20), LocalDateTime.now(), TipoCupon.UNICO, EstadoCupon.NO_DISPONIBLE);

        cuponService.editarCupon(dto, cupon.getId());

        Cupon actualizado = cuponRepository.findById(cupon.getId()).orElseThrow();
        assertEquals("Actualizado", actualizado.getNombre());
        assertEquals(EstadoCupon.NO_DISPONIBLE, actualizado.getEstado());
    }

    @Test
    void eliminarCupon_exitoso() throws CuponException {
        Cupon cupon = new Cupon("Eliminar", "DEL123", LocalDateTime.now().plusDays(5),
                LocalDateTime.now(), 10.0f, TipoCupon.MULTIPLE, EstadoCupon.DISPONIBLE, false);
        cuponRepository.save(cupon);

        cuponService.eliminarCupon(cupon.getId());

        assertFalse(cuponRepository.findById(cupon.getId()).isPresent());
    }

    @Test
    void obtenerInformacionCupon_ok() throws CuponException {
        Cupon cupon = new Cupon("Info", "INFO321", LocalDateTime.now().plusDays(5),
                LocalDateTime.now(), 20.0f, TipoCupon.MULTIPLE, EstadoCupon.DISPONIBLE, false);
        cuponRepository.save(cupon);

        InformacionCuponDTO info = cuponService.obtenerInformacionCupon(cupon.getId());
        assertEquals("INFO321", info.codigo());
        assertEquals(20.0f, info.porcentajeDescuento());
    }

    @Test
    void aplicarCupon_valido_unico_noUsado() throws CuponException {
        Cupon cupon = new Cupon("Unico", "UNIQ1", LocalDateTime.now().plusDays(5),
                LocalDateTime.now(), 25.0f, TipoCupon.UNICO, EstadoCupon.DISPONIBLE, false);
        cuponRepository.save(cupon);

        Cupon aplicado = cuponService.aplicarCupon(cupon.getId(), LocalDateTime.now());
        assertTrue(aplicado.isUtilizado());
    }

    @Test
    void aplicarCupon_expirado_lanzaExcepcion() {
        Cupon cupon = new Cupon("Vencido", "EXP123", LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(10), 10.0f, TipoCupon.MULTIPLE, EstadoCupon.DISPONIBLE, false);
        cuponRepository.save(cupon);

        assertThrows(CuponException.class, () ->
                cuponService.aplicarCupon(cupon.getId(), LocalDateTime.now()));
    }

    @Test
    void obtenerCuponesFiltrados_porNombreYEstado() {
        Cupon cupon = new Cupon("Promo2024", "PROMO24", LocalDateTime.now().plusDays(10),
                LocalDateTime.now(), 15.0f, TipoCupon.MULTIPLE, EstadoCupon.DISPONIBLE, false);
        cuponRepository.save(cupon);

        ItemsCuponFiltroDTO filtro = new ItemsCuponFiltroDTO("Promo2024", null, null,
                null, null, EstadoCupon.DISPONIBLE);

        List<ItemsCuponDTO> resultado = cuponService.obtenerCuponesFiltrados(filtro);
        assertFalse(resultado.isEmpty());
    }

    @Test
    void getAllDisponibles_retornaResultados() {
        Cupon cupon = new Cupon("Activo", "ACT123", LocalDateTime.now().plusDays(3),
                LocalDateTime.now(), 10.0f, TipoCupon.MULTIPLE, EstadoCupon.DISPONIBLE, false);
        cuponRepository.save(cupon);

        var page = cuponService.getAllDisponibles(PageRequest.of(0, 10));
        assertFalse(page.isEmpty());
    }

    @Test
    void getAllNoDisponibles_vacioSinRegistros() {
        var page = cuponService.getAllNoDisponibles(PageRequest.of(0, 10));
        assertTrue(page.isEmpty());
    }
}
