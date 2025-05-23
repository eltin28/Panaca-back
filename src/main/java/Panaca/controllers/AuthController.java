package Panaca.controllers;

import Panaca.dto.autenticacion.MensajeDTO;
import Panaca.dto.autenticacion.TokenDTO;
import Panaca.dto.cuenta.*;
import Panaca.exceptions.CarritoException;
import Panaca.exceptions.CuentaException;
import Panaca.service.service.CuentaService;
import Panaca.service.service.CuponService;
import Panaca.service.service.EmailService;
import Panaca.service.service.EventoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    CuentaService cuentaService;
    @Autowired
    EmailService emailService;
    @Autowired
    CuponService cuponService;
    @Autowired
    EventoService eventoService;

    /**
     * Crea la cuenta de un nuevo usuario a partir de los datos proporcionados.
     *
     * @param cuentaDTO DTO con los datos del nuevo usuario.
     * @return ResponseEntity con un MensajeDTO que indica el resultado de la operación.
     * @throws CuentaException si ocurre un error en la lógica de creación de la cuenta.
     * @throws CarritoException si ocurre un error al asociar el carrito inicial del usuario.
     */
    @PostMapping("/crear-cuenta")
    public ResponseEntity<MensajeDTO<String>> crearCuenta(@Valid @RequestBody CrearCuentaDTO cuentaDTO) throws CuentaException, CarritoException {
        try {
            cuentaService.crearCuenta(cuentaDTO);
            return ResponseEntity.ok(new MensajeDTO<>(true, "Cuenta creada correctamente"));
        } catch (CuentaException | CarritoException e) {
            return ResponseEntity.badRequest().body(new MensajeDTO<>(false, e.getMessage()));
        }
    }

    /**
     * Verifica el código de registro enviado al correo del usuario.
     *
     * @param validarCodigoDTO DTO con email y código de verificación.
     * @return ResponseEntity con mensaje de activación o error.
     * @throws CuentaException si el código no es válido, expiró o no se encuentra la cuenta.
     */
    @PostMapping("/validar-codigo-registro")
    public ResponseEntity<MensajeDTO<String>> validarCodigo(@Valid @RequestBody ValidarCodigoDTO validarCodigoDTO) throws CuentaException {
        try {
            cuentaService.validarCodigo(validarCodigoDTO);
            return ResponseEntity.ok(new MensajeDTO<>(true, "Cuenta activada con éxito"));
        } catch (CuentaException e) {
            return ResponseEntity.badRequest().body(new MensajeDTO<>(false, e.getMessage()));
        }
    }

    /**
     * Envía un código de recuperación de contraseña al correo del usuario.
     *
     * @param codigoContraseniaDTO DTO con el email del usuario.
     * @return Mensaje de confirmación o error según el resultado.
     * @throws CuentaException si no se encuentra la cuenta asociada.
     */
    @PostMapping("/enviar-codigo-recuperacion-contasenia")
    public ResponseEntity<MensajeDTO<String>> enviarCodigoRecuperacion(@Valid @RequestBody CodigoContraseniaDTO codigoContraseniaDTO) throws CuentaException {
        try {
            cuentaService.enviarCodigoRecuperacionPassword(codigoContraseniaDTO);
            return ResponseEntity.ok(new MensajeDTO<>(false, "Código enviado correctamente al correo electrónico."));
        } catch (CuentaException e) {
            return ResponseEntity.badRequest().body(new MensajeDTO<>(false, e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MensajeDTO<>(false, "Error al enviar el correo"));
        }
    }

    /**
     * Modifica la contraseña de un usuario o administrador a partir de un código de verificación.
     *
     * @param cambiarPasswordDTO DTO con el código de verificación y la nueva contraseña.
     * @return ResponseEntity con mensaje de confirmación o error.
     * @throws CuentaException si el código no es válido o no se encuentra la cuenta.
     */
    @PutMapping("/cambiar-password")
    public ResponseEntity<MensajeDTO<String>> cambiarPassword(@Valid @RequestBody CambiarPasswordDTO cambiarPasswordDTO) throws CuentaException {
        try {
            cuentaService.cambiarPassword(cambiarPasswordDTO);
            return ResponseEntity.ok(new MensajeDTO<>(false, "Contraseña modificada con éxito"));
        } catch (CuentaException e) {
            return ResponseEntity.badRequest().body(new MensajeDTO<>(false, e.getMessage()));
        }
    }

    //==================================== METODOS AUTENTICACION =============================================//

    /**
     * Inicia sesión de usuario y genera un token JWT.
     *
     * @param loginDTO DTO con las credenciales de acceso.
     * @return ResponseEntity con el TokenDTO si las credenciales son válidas.
     * @throws Exception si ocurre un error en la autenticación.
     */
    @PostMapping("/iniciar-sesion")
    public ResponseEntity<MensajeDTO<TokenDTO>> iniciarSesion(@Valid @RequestBody LoginDTO loginDTO) throws Exception {
        TokenDTO token = cuentaService.iniciarSesion(loginDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, token));
    }
}