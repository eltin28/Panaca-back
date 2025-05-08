package Panaca.ServiceTest;

import Panaca.configs.JWTUtils;
import Panaca.dto.autenticacion.TokenDTO;
import Panaca.dto.cuenta.EditarCuentaDTO;
import Panaca.dto.cuenta.InformacionCuentaDTO;
import Panaca.dto.cuenta.LoginDTO;
import Panaca.exceptions.CuentaException;
import Panaca.model.documents.Cuenta;
import Panaca.model.enums.EstadoCuenta;
import Panaca.model.enums.Rol;
import Panaca.repository.CuentaRepository;
import Panaca.service.implement.CuentaServiceImp;
import Panaca.service.service.CarritoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class CuentaServiceTest {

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private CarritoService carritoService;

    @Mock
    private JWTUtils jwtUtils;

    @InjectMocks
    private CuentaServiceImp cuentaService;

    private Cuenta cuentaActiva;
    private Cuenta cuentaInactiva;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cuentaActiva = new Cuenta();
        cuentaActiva.setId("66a2a9aaa8620e3c1c5437be");
        cuentaActiva.setCedula("1234567890");
        cuentaActiva.setNombre("Juan Pérez");
        cuentaActiva.setTelefono("3001234567");
        cuentaActiva.setEmail("juan@example.com");
        cuentaActiva.setPassword("$2a$10$hashedpassword");
        cuentaActiva.setRol(Rol.CLIENTE);
        cuentaActiva.setEstado(EstadoCuenta.ACTIVO);

        cuentaInactiva = new Cuenta();
        cuentaInactiva.setId("66a2c14dd9219911cd34f2c0");
        cuentaInactiva.setCedula("1098765432");
        cuentaInactiva.setNombre("Maria Gomez");
        cuentaInactiva.setTelefono("3017654321");
        cuentaInactiva.setEmail("maria@example.com");
        cuentaInactiva.setPassword("$2a$10$hashedpassword");
        cuentaInactiva.setRol(Rol.CLIENTE);
        cuentaInactiva.setEstado(EstadoCuenta.INACTIVO);
    }

    @Test
    void iniciarSesion_exitoso() throws CuentaException {
        when(cuentaRepository.findByEmail("juan@example.com")).thenReturn(Optional.of(cuentaActiva));
        when(jwtUtils.generarToken(eq("juan@example.com"), anyMap())).thenReturn("token_mock");

        LoginDTO loginDTO = new LoginDTO("juan@example.com", "plaintextPassword");
        TokenDTO token = cuentaService.iniciarSesion(loginDTO);

        assertNotNull(token);
        assertEquals("token_mock", token.token());
    }

    @Test
    void iniciarSesion_cuentaInactiva_lanzaExcepcion() {
        when(cuentaRepository.findByEmail("maria@example.com")).thenReturn(Optional.of(cuentaInactiva));
        LoginDTO loginDTO = new LoginDTO("maria@example.com", "password");

        CuentaException exception = assertThrows(CuentaException.class, () -> cuentaService.iniciarSesion(loginDTO));
        assertEquals("La cuenta no está activa.", exception.getMessage());
    }

    @Test
    void obtenerInformacionCuenta_devuelveDTO() throws CuentaException {
        when(cuentaRepository.findById("66a2c14dd9219911cd34f2c0")).thenReturn(Optional.of(cuentaActiva));
        InformacionCuentaDTO info = cuentaService.obtenerInformacionCuenta("66a2c14dd9219911cd34f2c0");

        assertNotNull(info);
        assertEquals("Juan Pérez", info.nombre());
    }

    @Test
    void eliminarCuenta_cambiaEstadoAInactivo() throws CuentaException {
        when(cuentaRepository.findById("66a2a9aaa8620e3c1c5437be")).thenReturn(Optional.of(cuentaActiva));

        cuentaService.eliminarCuenta("66a2a9aaa8620e3c1c5437be");

        assertEquals(EstadoCuenta.INACTIVO, cuentaActiva.getEstado());
        verify(cuentaRepository).save(cuentaActiva);
    }

    @Test
    void editarCuenta_modificaNombreTelefonoYPassword() throws CuentaException {
        when(cuentaRepository.findById("66a2c14dd9219911cd34f2c0")).thenReturn(Optional.of(cuentaActiva));
        EditarCuentaDTO editarDTO = new EditarCuentaDTO("66a2c14dd9219911cd34f2c0", "Nuevo Nombre", "3210000000", "nuevaPassword");

        cuentaService.editarCuenta(editarDTO);

        assertEquals("Nuevo Nombre", cuentaActiva.getNombre());
        assertEquals("3210000000", cuentaActiva.getTelefono());
        verify(cuentaRepository).save(cuentaActiva);
    }
}