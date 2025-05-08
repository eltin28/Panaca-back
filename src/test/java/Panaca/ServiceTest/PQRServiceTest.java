package Panaca.ServiceTest;

import Panaca.dto.PQR.CrearPQRDTO;
import Panaca.dto.PQR.InformacionPQRDTO;
import Panaca.dto.PQR.ItemPQRDTO;
import Panaca.dto.PQR.ResponderPQRDTO;
import Panaca.exceptions.PQRException;
import Panaca.model.documents.Cuenta;
import Panaca.model.documents.PQR;
import Panaca.model.enums.CategoriaPQR;
import Panaca.model.enums.EstadoPQR;
import Panaca.repository.CuentaRepository;
import Panaca.repository.PQRRepository;
import Panaca.service.implement.PQRServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class PQRServiceTest {

    @Mock
    private PQRRepository pqrRepository;
    @Mock private CuentaRepository cuentaRepository;
    @InjectMocks
    private PQRServiceImp pqrService;

    private PQR pqr;
    private Cuenta cuenta;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        cuenta = new Cuenta();
        cuenta.setId("u123");
        cuenta.setNombre("Carlos Diaz");
        cuenta.setTelefono("3102223344");
        cuenta.setEmail("carlos@example.com");

        pqr = new PQR();
        pqr.setId("pqr001");
        pqr.setIdUsuario("u123");
        pqr.setFechaCreacion(LocalDateTime.now());
        pqr.setEstadoPQR(EstadoPQR.PENDIENTE);
        pqr.setCategoriaPQR(CategoriaPQR.RECLAMO);
        pqr.setDescripcion("Problema con la reserva");
    }

    @Test
    void crearPQR_deberiaGuardarCorrectamente() throws PQRException {
        CrearPQRDTO dto = new CrearPQRDTO("u123", CategoriaPQR.RECLAMO, "Problema con el servicio");
        pqrService.crearPQR(dto);
        verify(pqrRepository).save(any(PQR.class));
    }

    @Test
    void eliminarPQR_cambiaEstadoAEliminado() throws PQRException {
        when(pqrRepository.findById("pqr001")).thenReturn(Optional.of(pqr));
        pqrService.eliminarPQR("pqr001");
        assertEquals(EstadoPQR.ELIMINADO, pqr.getEstadoPQR());
        verify(pqrRepository).save(pqr);
    }

    @Test
    void obtenerInformacionPQR_devuelveDTO() throws PQRException {
        when(pqrRepository.findById("pqr001")).thenReturn(Optional.of(pqr));
        when(cuentaRepository.findById("u123")).thenReturn(Optional.of(cuenta));
        InformacionPQRDTO dto = pqrService.obtenerInformacionPQR("pqr001");
        assertEquals("Carlos Diaz", dto.nombreUsuario());
    }

    @Test
    void obtenerPQRsPorUsuario_devuelveLista() throws PQRException {
        when(pqrRepository.findByIdUsuario("u123")).thenReturn(List.of(pqr));
        List<PQR> resultado = pqrService.obtenerPQRsPorUsuario("u123");
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
    }

    @Test
    void responderPQR_cambiaEstadoYAsignaRespuesta() throws PQRException {
        when(pqrRepository.findById("pqr001")).thenReturn(Optional.of(pqr));
        ResponderPQRDTO dto = new ResponderPQRDTO("pqr001", "Ya fue solucionado");
        pqrService.responderPQR(dto);
        assertEquals(EstadoPQR.RESUELTO, pqr.getEstadoPQR());
        assertEquals("Ya fue solucionado", pqr.getRespuesta());
        assertNotNull(pqr.getFechaRespuesta());
        verify(pqrRepository).save(pqr);
    }

    @Test
    void listarPQRs_retornaDTOs() throws PQRException {
        when(pqrRepository.findAll()).thenReturn(List.of(pqr));
        List<ItemPQRDTO> lista = pqrService.listarPQRs();
        assertFalse(lista.isEmpty());
        assertEquals("pqr001", lista.get(0).id());
    }
}
