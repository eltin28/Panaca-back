package Panaca.controllers;

import Panaca.dto.PQR.InformacionPQRDTO;
import Panaca.dto.PQR.ResponderPQRDTO;
import Panaca.dto.autenticacion.MensajeDTO;
import Panaca.dto.cupon.CrearCuponDTO;
import Panaca.dto.cupon.EditarCuponDTO;
import Panaca.dto.cupon.InformacionCuponDTO;
import Panaca.dto.cupon.ItemsCuponDTO;
import Panaca.dto.evento.CrearEventoDTO;
import Panaca.dto.evento.EditarEventoDTO;
import Panaca.dto.evento.ItemEventoDTO;
import Panaca.exceptions.CuponException;
import Panaca.exceptions.EventoException;
import Panaca.exceptions.PQRException;
import Panaca.model.documents.Cupon;
import Panaca.service.service.CuponService;
import Panaca.service.service.EventoService;
import Panaca.service.service.ImagesService;
import Panaca.dto.evento.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class AdminController {

    @Autowired
    EventoService eventoService;
    @Autowired
    ImagesService imagesService;
    @Autowired
    CuponService cuponService;
    @Autowired
    Panaca.service.service.PQRService PQRService;

    @PostMapping("/crear-evento")
    public ResponseEntity<MensajeDTO<String>> crearEvento(@Valid @RequestBody CrearEventoDTO eventoDTO) throws EventoException {
        try {
            eventoService.crearEvento(eventoDTO);
            return ResponseEntity.ok(new MensajeDTO<>(true, "Evento creado exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MensajeDTO<>(false, e.getMessage()));
        }
    }

    @PutMapping("/editar-evento/{id}")
    public ResponseEntity<MensajeDTO> editarEvento(@PathVariable("id") String id, @Valid @RequestBody EditarEventoDTO eventoDTO) throws EventoException {
        try{
            eventoService.editarEvento(id,eventoDTO);
            return ResponseEntity.ok(new MensajeDTO<>(true, "Evento modificado exitosamente"));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(new MensajeDTO<>(false, e.getMessage()));
            }
    }

    @DeleteMapping("/eliminar-evento/{id}")
    public ResponseEntity<MensajeDTO<String>> eliminarEvento(@Valid @PathVariable String id) throws EventoException {
        try{
            eventoService.eliminarEvento(id);
            return ResponseEntity.ok(new MensajeDTO<>(true, "Evento eliminado exitosamente"));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(new MensajeDTO<>(false, e.getMessage()));
        }
    }

    @GetMapping("/evento-activos")
    public ResponseEntity<Page<ItemEventoDTO>> getEventosActivos(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "3") int size){
        PageRequest pageRequest= PageRequest.of(page, size);
        Page<ItemEventoDTO> eventos = eventoService.getEventoActivos(pageRequest);
        return ResponseEntity.ok(eventos);
    }

    @GetMapping("/evento-inactivos")
    public ResponseEntity<Page<ItemEventoDTO>> getEventosInactivos(@RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "3") int size){
        PageRequest pageRequest= PageRequest.of(page, size);
        Page<ItemEventoDTO> eventos = eventoService.getEventosInactivos(pageRequest);
        return ResponseEntity.ok(eventos);

    }

//    @GetMapping("/obtener-localidad/{nombre}")
//    public ResponseEntity<MensajeDTO<Localidad>> obtenerLocalidad(@Valid @PathVariable String nombre) throws EventoException {
//        Localidad info = eventoService.obtenerLocalidadPorNombre(nombre);
//        return ResponseEntity.ok(new MensajeDTO<>(true, info ));
//    }

    //==================================== METODOS PQR =============================================//

    /**
     * Método para responder una PQR.
     * @param responderPQRDTO Datos de respuesta a la PQR.
     * @return Mensaje de éxito.
     */
    @PutMapping("/responder-pqr")
    public ResponseEntity<MensajeDTO<String>> responderPQR(@Valid @RequestBody ResponderPQRDTO responderPQRDTO) {
        try {
            PQRService.responderPQR(responderPQRDTO);
            return ResponseEntity.ok(new MensajeDTO<>(true,"PQR respondida exitosamente"));
        } catch (PQRException e) {
            return ResponseEntity.badRequest().body(new MensajeDTO<>(false,e.getMessage()));
        }
    }

    /**
     * Método para obtener información de una PQR por su ID.
     * @param id ID de la PQR.
     * @return Información de la PQR.
     */
    @GetMapping("/obterner-informacion-pqr/{id}")
    public ResponseEntity<MensajeDTO<InformacionPQRDTO>> obtenerInformacionPQR(@Valid @PathVariable String id) throws PQRException {
        InformacionPQRDTO informacionPQRDTO = PQRService.obtenerInformacionPQR(id);
        return ResponseEntity.ok(new MensajeDTO<>(true, informacionPQRDTO));
    }

    /**
     * Método para eliminar una PQR por su ID.
     * @param id ID de la PQR a eliminar.
     * @return Mensaje de éxito.
     */
    @DeleteMapping("/eliminar-pqr/{id}")
    public ResponseEntity<MensajeDTO<String>> eliminarPQR(@Valid @PathVariable String id) {
        try {
            PQRService.eliminarPQR(id);
            return ResponseEntity.ok(new MensajeDTO<>(true,"PQR eliminado con exito"));
        } catch (PQRException e) {
            return ResponseEntity.badRequest().body(new MensajeDTO<>(false,e.getMessage()));
        }
    }

    //====================================== METODOS CUPON ====================================//

    @PostMapping("/crear-cupon")
    public ResponseEntity<MensajeDTO<String>> crearCupon(@Valid @RequestBody CrearCuponDTO cuponDTO) throws CuponException {
        try {
            cuponService.crearCupon(cuponDTO);
            return ResponseEntity.ok(new MensajeDTO<>(true,"Cupon creado con exito"));
        } catch (CuponException e) {
            return ResponseEntity.badRequest().body(new MensajeDTO<>(false,e.getMessage()));
            }
        }

    @PutMapping("/editar-cupon/{cuponId}")
    public ResponseEntity<MensajeDTO<String>> editarCupon(@Valid @RequestBody EditarCuponDTO cupon, @PathVariable String cuponId) throws CuponException {
        try {
            cuponService.editarCupon(cupon, cuponId);
            return ResponseEntity.ok(new MensajeDTO<>(true, "Cupón editado con éxito"));
        } catch (CuponException e) {
            return ResponseEntity.badRequest().body(new MensajeDTO<>(false, e.getMessage()));
        }
    }

    @DeleteMapping("/eliminar-cupon/{id}")
    public ResponseEntity<MensajeDTO<String>> eliminarCupon(@PathVariable String id) throws CuponException {
        try {
            cuponService.eliminarCupon(id);
            return ResponseEntity.ok(new MensajeDTO<>(true, "Cupón eliminado con éxito"));
        } catch (CuponException e) {
            return ResponseEntity.badRequest().body(new MensajeDTO<>(false, e.getMessage()));
        }
    }

    @GetMapping("/cupon/{id}")
    public ResponseEntity<InformacionCuponDTO> obtenerInformacionCupon(@PathVariable String id) throws CuponException {
        InformacionCuponDTO cuponInfo = cuponService.obtenerInformacionCupon(id);
        return ResponseEntity.ok(cuponInfo);
    }

    @GetMapping("/cupones-disponibles")
    public ResponseEntity<Page<Cupon>> getAllDisponibles(@RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "4") int size){
        PageRequest pageRequest= PageRequest.of(page, size);
        Page<Cupon> cupones = cuponService.getAllDisponibles(pageRequest);
        return ResponseEntity.ok(cupones);
    }

    @GetMapping("/cupones-no-disponibles")
    public ResponseEntity<Page<Cupon>> getAllNoDisponibles(@RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "4") int size){
        PageRequest pageRequest= PageRequest.of(page, size);
        Page<Cupon> cupones = cuponService.getAllNoDisponibles(pageRequest);
        return ResponseEntity.ok(cupones);
    }

    @PostMapping("/filtrar-cupones")
    public ResponseEntity<List<ItemsCuponDTO>> obtenerCuponesFiltrados(@RequestBody ItemsCuponDTO itemCuponDTO) {
        List<ItemsCuponDTO> cuponesFiltrados = cuponService.obtenerCuponesFiltrados(itemCuponDTO);
        return ResponseEntity.ok(cuponesFiltrados);
    }

    //=========================================== METODOS IMAGENES ===============================================//

    @PostMapping("/subir")
    public ResponseEntity<MensajeDTO<String>> subir(@RequestParam("imagen") MultipartFile imagen) throws Exception {
        String respuesta = imagesService.subirImagen(imagen);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, respuesta));
    }

    @DeleteMapping("/eliminar/{idImagen}")
    public ResponseEntity<MensajeDTO<String>> eliminar(@PathVariable String idImagen)  throws Exception{
        try {
            imagesService.eliminarImagen( idImagen );
            return ResponseEntity.ok().body(new MensajeDTO<>(false, "La imagen fue eliminada correctamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MensajeDTO<>(false, e.getMessage()));
        }
    }




}
