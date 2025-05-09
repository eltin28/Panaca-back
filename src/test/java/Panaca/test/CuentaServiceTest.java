package Panaca.test;

import Panaca.dto.autenticacion.TokenDTO;
import Panaca.dto.cuenta.*;
import Panaca.model.documents.Carrito;
import Panaca.model.documents.Cuenta;
import Panaca.model.enums.EstadoCuenta;
import Panaca.model.enums.Rol;
import Panaca.repository.CarritoRepository;
import Panaca.repository.CuentaRepository;
import Panaca.repository.CuponRepository;
import Panaca.service.service.CuentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.UUID;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CuentaServiceTest {

    @Autowired
    private CuentaService cuentaService;

    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private CuponRepository cuponRepository;

    @Test
    public void crearCuentaTest() {
        // Datos únicos para evitar conflictos por claves duplicadas
        String email = UUID.randomUUID() + "@test.com";
        String cedula = String.valueOf((int)(Math.random() * 1_000_000_000)); // 9 dígitos aleatorios

        CrearCuentaDTO cuentaDTO = new CrearCuentaDTO(
                cedula,
                "Juan Test",
                "3123456789",
                email,
                "password123"
        );

        assertDoesNotThrow(() -> {
            cuentaService.crearCuenta(cuentaDTO);
        });

        // Verificar que la cuenta fue guardada
        Optional<Cuenta> cuentaOpt = cuentaRepository.findByEmail(email);
        assertTrue(cuentaOpt.isPresent());

        Cuenta cuenta = cuentaOpt.get();
        assertEquals(email, cuenta.getEmail());
        assertEquals(EstadoCuenta.INACTIVO, cuenta.getEstado());
        assertEquals(Rol.CLIENTE, cuenta.getRol());

        // Verificar que el carrito fue creado correctamente
        Optional<Carrito> carritoOpt = carritoRepository.findByIdUsuario(cuenta.getId());
        assertTrue(carritoOpt.isPresent());

        Carrito carrito = carritoOpt.get();
        assertEquals(cuenta.getId(), carrito.getIdUsuario());
        assertTrue(carrito.getItems().isEmpty());
    }

    @Test
    public void crearCuenta_conEmailExistente_debeLanzarExcepcion() {
        // Crear primero una cuenta válida
        String email = UUID.randomUUID() + "@test.com";
        String cedula = String.valueOf((int)(Math.random() * 1_000_000_000));

        CrearCuentaDTO cuentaDTO = new CrearCuentaDTO(
                cedula,
                "Usuario Original",
                "3000000000",
                email,
                "password123"
        );

        assertDoesNotThrow(() -> cuentaService.crearCuenta(cuentaDTO));

        // Intentar crear otra cuenta con mismo email pero distinta cédula
        CrearCuentaDTO cuentaDTOEmailDuplicado = new CrearCuentaDTO(
                String.valueOf((int)(Math.random() * 1_000_000_000)), // cédula distinta
                "Usuario Duplicado",
                "3111111111",
                email, // mismo email
                "password123"
        );

        Exception ex = assertThrows(Exception.class, () -> cuentaService.crearCuenta(cuentaDTOEmailDuplicado));
        assertTrue(ex.getMessage().contains("email"));
    }

    @Test
    public void crearCuenta_conCedulaExistente_debeLanzarExcepcion() {
        // Crear primero una cuenta válida
        String email = UUID.randomUUID() + "@test.com";
        String cedula = String.valueOf((int)(Math.random() * 1_000_000_000));

        CrearCuentaDTO cuentaDTO = new CrearCuentaDTO(
                cedula,
                "Usuario Original",
                "3000000000",
                email,
                "password123"
        );

        assertDoesNotThrow(() -> cuentaService.crearCuenta(cuentaDTO));

        // Intentar crear otra cuenta con misma cédula pero distinto email
        CrearCuentaDTO cuentaDTOCedulaDuplicada = new CrearCuentaDTO(
                cedula, // misma cédula
                "Usuario Duplicado",
                "3222222222",
                UUID.randomUUID() + "@test.com", // email distinto
                "password123"
        );

        Exception ex = assertThrows(Exception.class, () -> cuentaService.crearCuenta(cuentaDTOCedulaDuplicada));
        assertTrue(ex.getMessage().contains("cedula"));
    }

    @Test
    public void editarCuentaTest() {
        // ID existente del dataset
        String idCuenta = "681e31265224563fa4763910";

        EditarCuentaDTO editarDTO = new EditarCuentaDTO(
                idCuenta,
                "Pepito Perez Actualizado",
                "3012345678",
                "nuevapassword123"
        );

        assertDoesNotThrow(() -> cuentaService.editarCuenta(editarDTO));

        Optional<Cuenta> cuentaOpt = cuentaRepository.findById(idCuenta);
        assertTrue(cuentaOpt.isPresent());

        Cuenta cuenta = cuentaOpt.get();
        assertEquals("Pepito Perez Actualizado", cuenta.getNombre());
        assertEquals("3012345678", cuenta.getTelefono());
        assertNotEquals("nuevapassword123", cuenta.getPassword()); // Password debe estar encriptada
    }

    @Test
    public void editarCuenta_conIdInexistente_debeLanzarExcepcion() {
        EditarCuentaDTO editarDTO = new EditarCuentaDTO(
                "000000000000000000000000", // ID inexistente
                "Nombre Invalido",
                "3000000000",
                "otraPassword123"
        );

        Exception ex = assertThrows(Exception.class, () -> cuentaService.editarCuenta(editarDTO));
        assertTrue(ex.getMessage().contains("id"));
    }

    @Test
    public void eliminarCuentaTest() {
        // ID existente desde el dataset
        String idCuenta = "681e31beabfedf71b4aa91f9";

        assertDoesNotThrow(() -> cuentaService.eliminarCuenta(idCuenta));

        Optional<Cuenta> cuentaOpt = cuentaRepository.findById(idCuenta);
        assertTrue(cuentaOpt.isPresent());

        Cuenta cuenta = cuentaOpt.get();
        assertEquals(EstadoCuenta.INACTIVO, cuenta.getEstado());
    }

    @Test
    public void eliminarCuenta_conIdInexistente_debeLanzarExcepcion() {
        String idFalso = "000000000000000000000000";

        Exception ex = assertThrows(Exception.class, () -> cuentaService.eliminarCuenta(idFalso));
        assertTrue(ex.getMessage().contains("id"));
    }

    @Test
    public void obtenerInformacionCuentaTest() {
        String idCuenta = "681e31265224563fa4763910";

        InformacionCuentaDTO info = assertDoesNotThrow(() -> cuentaService.obtenerInformacionCuenta(idCuenta));

        assertEquals("681e31265224563fa4763910", info.id());
        assertEquals("531489543", info.cedula());
        assertEquals("Pepito Perez Actualizado", info.nombre());
        assertEquals("3012345678", info.telefono());
        assertEquals("fcf07767-4f3c-4835-86a6-5c6b259552f9@test.com", info.email());
    }

    @Test
    public void obtenerInformacionCuenta_conIdInexistente_debeLanzarExcepcion() {
        String idFalso = "000000000000000000000000";

        Exception ex = assertThrows(Exception.class, () -> cuentaService.obtenerInformacionCuenta(idFalso));
        assertTrue(ex.getMessage().contains("id"));
    }

    @Test
    public void enviarCodigoRecuperacionPasswordTest() {
        String email = "fcf07767-4f3c-4835-86a6-5c6b259552f9@test.com"; // del dataset

        CodigoContraseniaDTO dto = new CodigoContraseniaDTO(email);

        assertDoesNotThrow(() -> cuentaService.enviarCodigoRecuperacionPassword(dto));

        Optional<Cuenta> cuentaOpt = cuentaRepository.findByEmail(email);
        assertTrue(cuentaOpt.isPresent());

        Cuenta cuenta = cuentaOpt.get();
        assertNotNull(cuenta.getCodigoVerificacionContrasenia());
        assertNotNull(cuenta.getFechaExpiracionCodigoContrasenia());
    }

    @Test
    public void enviarCodigoRecuperacionPassword_emailInvalido_debeLanzarExcepcion() {
        String email = "noexiste@test.com";

        CodigoContraseniaDTO dto = new CodigoContraseniaDTO(email);

        Exception ex = assertThrows(Exception.class, () -> cuentaService.enviarCodigoRecuperacionPassword(dto));
        assertTrue(ex.getMessage().toLowerCase().contains("cuenta"));
    }

    @Test
    public void cambiarPassword_codigoValido_actualizaContrasenia() {
        // Cuenta válida del dataset
        String email = "fcf07767-4f3c-4835-86a6-5c6b259552f9@test.com";

        // Setear código de recuperación directamente
        Cuenta cuenta = cuentaRepository.findByEmail(email).get();
        cuenta.setCodigoVerificacionContrasenia("ABCDE");
        cuenta.setFechaExpiracionCodigoContrasenia(LocalDateTime.now().plusMinutes(10));
        cuentaRepository.save(cuenta);

        CambiarPasswordDTO dto = new CambiarPasswordDTO("ABCDE", "nuevaPass123");

        assertDoesNotThrow(() -> cuentaService.cambiarPassword(dto));

        Cuenta cuentaActualizada = cuentaRepository.findByEmail(email).get();
        assertNull(cuentaActualizada.getCodigoVerificacionContrasenia());
        assertNull(cuentaActualizada.getFechaExpiracionCodigoContrasenia());
    }

    @Test
    public void cambiarPassword_codigoInexistente_lanzaExcepcion() {
        CambiarPasswordDTO dto = new CambiarPasswordDTO("XXXX", "otraClave123");

        Exception ex = assertThrows(Exception.class, () -> cuentaService.cambiarPassword(dto));
        assertTrue(ex.getMessage().toLowerCase().contains("inválido"));
    }

    @Test
    public void cambiarPassword_codigoExpirado_lanzaExcepcionYReenvia() {
        String email = "fcf07767-4f3c-4835-86a6-5c6b259552f9@test.com";

        Cuenta cuenta = cuentaRepository.findByEmail(email).get();
        cuenta.setCodigoVerificacionContrasenia("ZZZZZ");
        cuenta.setFechaExpiracionCodigoContrasenia(LocalDateTime.now().minusMinutes(1)); // ya expiró
        cuentaRepository.save(cuenta);

        CambiarPasswordDTO dto = new CambiarPasswordDTO("ZZZZZ", "claveNueva2024");

        Exception ex = assertThrows(Exception.class, () -> cuentaService.cambiarPassword(dto));
        assertTrue(ex.getMessage().toLowerCase().contains("expirado"));
    }

    @Test
    public void validarCodigo_codigoCorrecto_activaCuentaYGeneraCupon() {
        String email = "fcf07767-4f3c-4835-86a6-5c6b259552f9@test.com";

        Cuenta cuenta = cuentaRepository.findByEmail(email).get();
        cuenta.setCodigoVerificacionRegistro("ABCDE");
        cuenta.setFechaExpiracionCodigo(LocalDateTime.now().plusMinutes(10));
        cuenta.setEstado(EstadoCuenta.INACTIVO);
        cuentaRepository.save(cuenta);

        ValidarCodigoDTO dto = new ValidarCodigoDTO(email, "ABCDE");

        assertDoesNotThrow(() -> cuentaService.validarCodigo(dto));

        Cuenta actualizada = cuentaRepository.findByEmail(email).get();
        assertEquals(EstadoCuenta.ACTIVO, actualizada.getEstado());
        assertNull(actualizada.getCodigoVerificacionRegistro());
        assertNull(actualizada.getFechaExpiracionCodigo());

        // Verificamos que se haya creado al menos un cupón
        assertFalse(cuponRepository.findAll().isEmpty());
    }

    @Test
    public void validarCodigo_codigoIncorrecto_lanzaExcepcion() {
        String email = "fcf07767-4f3c-4835-86a6-5c6b259552f9@test.com";

        Cuenta cuenta = cuentaRepository.findByEmail(email).get();
        cuenta.setCodigoVerificacionRegistro("ABCDE");
        cuenta.setFechaExpiracionCodigo(LocalDateTime.now().plusMinutes(10));
        cuentaRepository.save(cuenta);

        ValidarCodigoDTO dto = new ValidarCodigoDTO(email, "ZZZZZ");

        Exception ex = assertThrows(Exception.class, () -> cuentaService.validarCodigo(dto));
        assertTrue(ex.getMessage().toLowerCase().contains("incorrecto"));
    }

    @Test
    public void validarCodigo_codigoExpirado_lanzaExcepcionYReenvia() {
        String email = "fcf07767-4f3c-4835-86a6-5c6b259552f9@test.com";

        Cuenta cuenta = cuentaRepository.findByEmail(email).get();
        cuenta.setCodigoVerificacionRegistro("ABCDE");
        cuenta.setFechaExpiracionCodigo(LocalDateTime.now().minusMinutes(1)); // Expirado
        cuentaRepository.save(cuenta);

        ValidarCodigoDTO dto = new ValidarCodigoDTO(email, "ABCDE");

        Exception ex = assertThrows(Exception.class, () -> cuentaService.validarCodigo(dto));
        assertTrue(ex.getMessage().toLowerCase().contains("expirado"));
    }

    @Test
    public void iniciarSesion_loginValido_retornaToken() {
        String email = "fcf07767-4f3c-4835-86a6-5c6b259552f9@test.com";

        // Preparar cuenta con password válida y estado ACTIVO
        Cuenta cuenta = cuentaRepository.findByEmail(email).get();
        String passwordPlano = "password123";
        cuenta.setPassword(new BCryptPasswordEncoder().encode(passwordPlano));
        cuenta.setEstado(EstadoCuenta.ACTIVO);
        cuentaRepository.save(cuenta);

        LoginDTO dto = new LoginDTO(email, passwordPlano);

        TokenDTO tokenDTO = assertDoesNotThrow(() -> cuentaService.iniciarSesion(dto));
        assertNotNull(tokenDTO.token());
    }
    @Test
    public void iniciarSesion_emailInexistente_lanzaExcepcion() {
        LoginDTO dto = new LoginDTO("noexiste@test.com", "cualquierpass");

        Exception ex = assertThrows(Exception.class, () -> cuentaService.iniciarSesion(dto));
        assertTrue(ex.getMessage().toLowerCase().contains("correo"));
    }

    @Test
    public void iniciarSesion_passwordIncorrecta_lanzaExcepcion() {
        String email = "fcf07767-4f3c-4835-86a6-5c6b259552f9@test.com";

        Cuenta cuenta = cuentaRepository.findByEmail(email).get();
        cuenta.setPassword(new BCryptPasswordEncoder().encode("passwordCorrecta"));
        cuenta.setEstado(EstadoCuenta.ACTIVO);
        cuentaRepository.save(cuenta);

        LoginDTO dto = new LoginDTO(email, "passwordIncorrecta");

        Exception ex = assertThrows(Exception.class, () -> cuentaService.iniciarSesion(dto));
        assertTrue(ex.getMessage().toLowerCase().contains("incorrecta"));
    }

    @Test
    public void iniciarSesion_cuentaInactiva_lanzaExcepcion() {
        String email = "fcf07767-4f3c-4835-86a6-5c6b259552f9@test.com";

        Cuenta cuenta = cuentaRepository.findByEmail(email).get();
        cuenta.setPassword(new BCryptPasswordEncoder().encode("password123"));
        cuenta.setEstado(EstadoCuenta.INACTIVO);
        cuentaRepository.save(cuenta);

        LoginDTO dto = new LoginDTO(email, "password123");

        Exception ex = assertThrows(Exception.class, () -> cuentaService.iniciarSesion(dto));
        assertTrue(ex.getMessage().toLowerCase().contains("no está activa"));
    }

}