package co.edu.uniquindio.unieventos.controllers;

import co.edu.uniquindio.unieventos.dto.cuenta.*;
import co.edu.uniquindio.unieventos.dto.autenticacion.MensajeDTO;
import co.edu.uniquindio.unieventos.model.documents.Cuenta;
import co.edu.uniquindio.unieventos.model.vo.Usuario;
import co.edu.uniquindio.unieventos.repository.CuentaRepository;
import co.edu.uniquindio.unieventos.service.service.CuentaService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.mail.event.MailEvent;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cuenta")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class CuentaController {

    @Autowired
    private CuentaRepository cuentaRepo;
    private final CuentaService cuentaService;

    @PostMapping("/crear-cuenta")
    public ResponseEntity<MensajeDTO<String>> crearCuenta(@Valid @RequestBody CrearCuentaDTO cuentaDTO) throws Exception {
        try {
            cuentaService.crearCuenta(cuentaDTO);
            return ResponseEntity.ok(new MensajeDTO<>(true, "Cuenta creada correctamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MensajeDTO<>(false, "Error al crear la cuenta"));
        }
    }

    @PutMapping("/editar-perfil")
    public ResponseEntity<MensajeDTO<String>> editarCuenta(@Valid @RequestBody EditarCuentaDTO cuenta) throws Exception{
        cuentaService.editarCuenta(cuenta);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Cuenta editada exitosamente"));
    }

//    @GetMapping("/validar-codigo")
//    public ResponseEntity<MensajeDTO<ValidarCodigoDTO>> validarCodigo(@Valid @RequestBody ValidarCodigoDTO validarCodigoDTO)throws Exception {
//        ValidarCodigoDTO validar = cuentaService.validarCodigo(validarCodigoDTO);
//        return ResponseEntity.ok(new MensajeDTO<>(true, validar));
//    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<MensajeDTO<String>> eliminarCuenta(@PathVariable String id) throws Exception {
        cuentaService.eliminarCuenta(id);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Cuenta eliminada exitosamente"));
    }

    @GetMapping("/obtener/{id}")
    public ResponseEntity<MensajeDTO<InformacionCuentaDTO>> obtenerInformacionCuenta(@PathVariable String id) throws Exception{
        InformacionCuentaDTO info = cuentaService.obtenerInformacionCuenta(id);
        return ResponseEntity.ok(new MensajeDTO<>(false, info));
    }

    @PostMapping("/enviar-codigo-recuperacion-contaseña")
    public ResponseEntity<MensajeDTO<String>> enviarCodigoRecuperacion(@Valid @RequestBody String correo) throws Exception{
        String enviarCodigoRecuperacion = cuentaService.enviarCodigoRecuperacionPassword(correo);
        return ResponseEntity.ok(new MensajeDTO<>(false, enviarCodigoRecuperacion));
    }

    @PutMapping("/cambiar-password")
    public ResponseEntity<MensajeDTO<String>> cambiarPassword(@Valid @RequestBody CambiarPasswordDTO cambiarPasswordDTO) throws Exception{
        String cambiarPassword = cuentaService.cambiarPassword(cambiarPasswordDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, cambiarPassword));
    }



    /**
     * Metodo para agregar un usuario a la base de datos
     * @param usuario
     * @return la respuesta de ese ususario creado a la base de datos
     */
    @PostMapping
    public ResponseEntity<?> saveUsuario(@RequestBody Cuenta cuenta){
        try {
            Cuenta userSave = cuentaRepo.save(cuenta);
            return new ResponseEntity<Cuenta>(userSave, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getCause().toString(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtiene la lista de usuario de la base de datos
     * @return la lista de los ususarios
     */
    @GetMapping
    public ResponseEntity<?> findAllUsuarios(){
        try {
            List<Cuenta> cuentas = cuentaRepo.findAll();
            return new ResponseEntity<List<Cuenta>>(cuentas, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getCause().toString(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtiene un usuario segun el id necesitado
     * @param id
     * @return el usuario encontrado por su id
     */
    @GetMapping(value ="/{id}")
    public ResponseEntity<?> findUsuarioById(@RequestParam String id){
        try{
            Optional<Cuenta> cuentas = cuentaRepo.findById(id);
            return new ResponseEntity<String>("Cuenta encontrada correctamente",HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<String>(e.getCause().toString(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Actualiza los datos de un usuario
     * @param usuario
     * @return la respuesta con el usuario actualizado
     */
    @PutMapping
    public ResponseEntity<?> updateUsuario(@RequestBody Cuenta cuenta){
        try {
            Cuenta userSave = cuentaRepo.save(cuenta);
            return new ResponseEntity<Cuenta>(userSave, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getCause().toString(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Elimina un usuario por su id
     * @param id
     * @return mensaje de avuso de que el usuario fue eliminado
     */
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteUsuario(@PathVariable("id") String id){
        try {
            cuentaRepo.deleteById(id);
            return new ResponseEntity<String>("Fue eliminado", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getCause().toString(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
