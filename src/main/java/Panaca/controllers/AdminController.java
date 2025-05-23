package Panaca.controllers;

import Panaca.dto.PQR.InformacionPQRDTO;
import Panaca.dto.PQR.ResponderPQRDTO;
import Panaca.dto.autenticacion.MensajeDTO;
import Panaca.dto.cupon.*;
import Panaca.dto.devolucion.CambiarEstadoDevolucionDTO;
import Panaca.dto.devolucion.DevolucionResponseDTO;
import Panaca.dto.donation.MostrarDonacionDTO;
import Panaca.dto.evento.CrearEventoDTO;
import Panaca.dto.evento.EditarEventoDTO;
import Panaca.exceptions.CuponException;
import Panaca.exceptions.EventoException;
import Panaca.exceptions.PQRException;
import Panaca.model.documents.Cupon;
import Panaca.service.service.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    PQRService PQRService;
    @Autowired
    DevolucionService devolucionService;
    @Autowired
    DonationService donationService;

    //==================================== METODOS EVENTO =============================================//
    /**
     * Crea un nuevo evento en el sistema. Solo accesible por usuarios con rol ADMIN.
     *
     * @param eventoDTO DTO con los datos del evento a crear.
     * @return ResponseEntity con mensaje de éxito o error según el resultado.
     * @throws EventoException si ocurre un error durante la creación del evento.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/crear-evento")
    public ResponseEntity<MensajeDTO<String>> crearEvento(@Valid @RequestBody CrearEventoDTO eventoDTO) throws EventoException {
        try {
            eventoService.crearEvento(eventoDTO);
            return ResponseEntity.ok(new MensajeDTO<>(true, "Evento creado exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MensajeDTO<>(false, e.getMessage()));
        }
    }

    /**
     * Edita un evento existente. Solo accesible por usuarios con rol ADMIN.
     *
     * @param id ID del evento a modificar.
     * @param eventoDTO DTO con los nuevos datos del evento.
     * @return ResponseEntity con mensaje de éxito o error según el resultado.
     * @throws EventoException si ocurre un error durante la edición.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/editar-evento/{id}")
    public ResponseEntity<MensajeDTO<String>> editarEvento(@PathVariable("id") String id, @Valid @RequestBody EditarEventoDTO eventoDTO) throws EventoException {
        try {
            eventoService.editarEvento(id, eventoDTO);
            return ResponseEntity.ok(new MensajeDTO<>(true, "Evento modificado exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MensajeDTO<>(false, e.getMessage()));
        }
    }

    /**
     * Elimina un evento del sistema. Solo accesible por usuarios con rol ADMIN.
     *
     * @param id ID del evento a eliminar.
     * @return ResponseEntity con mensaje de éxito o error según el resultado.
     * @throws EventoException si ocurre un error durante la eliminación.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/eliminar-evento/{id}")
    public ResponseEntity<MensajeDTO<String>> eliminarEvento(@Valid @PathVariable String id) throws EventoException {
        try {
            eventoService.eliminarEvento(id);
            return ResponseEntity.ok(new MensajeDTO<>(true, "Evento eliminado exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MensajeDTO<>(false, e.getMessage()));
        }
    }

    //==================================== METODOS PQR =============================================//

    /**
     * Responde una PQR pendiente. Solo accesible por usuarios con rol ADMIN.
     *
     * @param responderPQRDTO DTO con el ID de la PQR y la respuesta del administrador.
     * @return ResponseEntity con mensaje de éxito o error según el resultado.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/responder-pqr")
    public ResponseEntity<MensajeDTO<String>> responderPQR(@Valid @RequestBody ResponderPQRDTO responderPQRDTO) {
        try {
            PQRService.responderPQR(responderPQRDTO);
            return ResponseEntity.ok(new MensajeDTO<>(true, "PQR respondida exitosamente"));
        } catch (PQRException e) {
            return ResponseEntity.badRequest().body(new MensajeDTO<>(false, e.getMessage()));
        }
    }

    /**
     * Obtiene la información detallada de una PQR por su ID. Solo accesible por usuarios con rol ADMIN.
     *
     * @param id ID de la PQR a consultar.
     * @return ResponseEntity con los detalles completos de la PQR en formato InformacionPQRDTO.
     * @throws PQRException si no se encuentra la PQR o hay un error en la consulta.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/obtener-informacion-pqr/{id}")
    public ResponseEntity<MensajeDTO<InformacionPQRDTO>> obtenerInformacionPQR(@Valid @PathVariable String id) throws PQRException {
        InformacionPQRDTO informacionPQRDTO = PQRService.obtenerInformacionPQR(id);
        return ResponseEntity.ok(new MensajeDTO<>(true, informacionPQRDTO));
    }

    /**
     * Elimina una PQR específica por su ID. Solo accesible por usuarios con rol ADMIN.
     *
     * @param id ID de la PQR a eliminar.
     * @return ResponseEntity con mensaje de éxito o error según el resultado.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/eliminar-pqr/{id}")
    public ResponseEntity<MensajeDTO<String>> eliminarPQR(@Valid @PathVariable String id) {
        try {
            PQRService.eliminarPQR(id);
            return ResponseEntity.ok(new MensajeDTO<>(true, "PQR eliminada con éxito"));
        } catch (PQRException e) {
            return ResponseEntity.badRequest().body(new MensajeDTO<>(false, e.getMessage()));
        }
    }

    //====================================== METODOS CUPON ====================================//

    /**
     * Crea un nuevo cupón promocional. Solo accesible por usuarios con rol ADMIN.
     *
     * @param cuponDTO DTO con los datos del cupón a crear.
     * @return ResponseEntity con mensaje de éxito o error según el resultado.
     * @throws CuponException si ocurre un error durante la creación del cupón.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/crear-cupon")
    public ResponseEntity<MensajeDTO<String>> crearCupon(@Valid @RequestBody CrearCuponDTO cuponDTO) throws CuponException {
        try {
            cuponService.crearCupon(cuponDTO);
            return ResponseEntity.ok(new MensajeDTO<>(true, "Cupón creado con éxito"));
        } catch (CuponException e) {
            return ResponseEntity.badRequest().body(new MensajeDTO<>(false, e.getMessage()));
        }
    }

    /**
     * Edita un cupón existente. Solo accesible por usuarios con rol ADMIN.
     *
     * @param cupon DTO con los nuevos datos del cupón.
     * @param cuponId ID del cupón a editar.
     * @return ResponseEntity con mensaje de éxito o error según el resultado.
     * @throws CuponException si ocurre un error durante la edición.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/editar-cupon/{cuponId}")
    public ResponseEntity<MensajeDTO<String>> editarCupon(@Valid @RequestBody EditarCuponDTO cupon, @PathVariable String cuponId) throws CuponException {
        try {
            cuponService.editarCupon(cupon, cuponId);
            return ResponseEntity.ok(new MensajeDTO<>(true, "Cupón editado con éxito"));
        } catch (CuponException e) {
            return ResponseEntity.badRequest().body(new MensajeDTO<>(false, e.getMessage()));
        }
    }

    /**
     * Elimina un cupón existente por su ID. Solo accesible por usuarios con rol ADMIN.
     *
     * @param id ID del cupón a eliminar.
     * @return ResponseEntity con mensaje de éxito o error según el resultado.
     * @throws CuponException si ocurre un error durante la eliminación del cupón.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/eliminar-cupon/{id}")
    public ResponseEntity<MensajeDTO<String>> eliminarCupon(@PathVariable String id) throws CuponException {
        try {
            cuponService.eliminarCupon(id);
            return ResponseEntity.ok(new MensajeDTO<>(true, "Cupón eliminado con éxito"));
        } catch (CuponException e) {
            return ResponseEntity.badRequest().body(new MensajeDTO<>(false, e.getMessage()));
        }
    }

    /**
     * Obtiene la información detallada de un cupón por su ID. Solo accesible por usuarios con rol ADMIN.
     *
     * @param id ID del cupón a consultar.
     * @return ResponseEntity con los datos del cupón en formato InformacionCuponDTO.
     * @throws CuponException si no se encuentra el cupón o hay un error en la consulta.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/cupon/{id}")
    public ResponseEntity<InformacionCuponDTO> obtenerInformacionCupon(@PathVariable String id) throws CuponException {
        InformacionCuponDTO cuponInfo = cuponService.obtenerInformacionCupon(id);
        return ResponseEntity.ok(cuponInfo);
    }

    /**
     * Filtra cupones según criterios específicos (nombre, fechas, tipo, estado, descuento).
     * Solo accesible por usuarios con rol ADMIN.
     *
     * @param filtro DTO con los criterios de búsqueda para los cupones.
     * @return ResponseEntity con la lista de cupones que cumplen los criterios.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/filtrar-cupones")
    public ResponseEntity<List<ItemsCuponDTO>> obtenerCuponesFiltrados(@RequestBody ItemsCuponFiltroDTO filtro) {
        List<ItemsCuponDTO> cuponesFiltrados = cuponService.obtenerCuponesFiltrados(filtro);
        return ResponseEntity.ok(cuponesFiltrados);
    }

    //=========================================== METODOS IMAGENES ===============================================//

    /**
     * Sube una imagen a Cloudinary. Solo accesible por usuarios con rol ADMIN.
     *
     * @param imagen Archivo recibido vía multipart.
     * @return ResponseEntity con la URL u objeto de respuesta de Cloudinary.
     * @throws Exception si ocurre un error en la subida.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/subir")
    public ResponseEntity<MensajeDTO<String>> subir(@RequestParam("imagen") MultipartFile imagen) throws Exception {
        String url = imagesService.subirImagen(imagen).get("url").toString(); // se asume clave "url" en la respuesta
        return ResponseEntity.ok(new MensajeDTO<>(false, url));
    }

    /**
     * Elimina una imagen en Cloudinary por su ID. Solo accesible por usuarios con rol ADMIN.
     *
     * @param idImagen ID público de la imagen en Cloudinary.
     * @return ResponseEntity con mensaje de confirmación o error.
     * @throws Exception si ocurre un fallo en la eliminación.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/eliminar/{idImagen}")
    public ResponseEntity<MensajeDTO<String>> eliminar(@PathVariable String idImagen) throws Exception {
        try {
            imagesService.eliminarImagen(idImagen);
            return ResponseEntity.ok(new MensajeDTO<>(false, "La imagen fue eliminada correctamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MensajeDTO<>(false, e.getMessage()));
        }
    }

    //==================================== METODOS DEVOLUCIONES =============================================//

    /**
     * Lista todas las solicitudes de devoluciones del sistema. Solo accesible por usuarios con rol ADMIN.
     *
     * @return ResponseEntity con la lista completa de devoluciones en formato DevolucionResponseDTO.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/devoluciones")
    public ResponseEntity<MensajeDTO<List<DevolucionResponseDTO>>> listarTodasDevoluciones() {
        List<DevolucionResponseDTO> devoluciones = devolucionService.listarTodas();
        return ResponseEntity.ok(new MensajeDTO<>(true, devoluciones));
    }

    /**
     * Cambia el estado de una solicitud de devolución. Solo accesible por ADMIN.
     *
     * @param id ID de la devolución.
     * @param dto DTO con el nuevo estado deseado.
     * @return ResponseEntity con los datos actualizados de la devolución.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/devoluciones/{id}/estado")
    public ResponseEntity<MensajeDTO<DevolucionResponseDTO>> cambiarEstadoDevolucion(
            @PathVariable String id,
            @Valid @RequestBody CambiarEstadoDevolucionDTO dto
    ) {
        DevolucionResponseDTO response = devolucionService.cambiarEstado(id, dto.nuevoEstado());
        return ResponseEntity.ok(new MensajeDTO<>(true, response));
    }


    //==================================== METODOS DONACIONES =============================================//

    /**
     * Obtiene el historial completo de todas las donaciones registradas en el sistema.
     * Solo accesible por usuarios con rol ADMIN.
     *
     * @return ResponseEntity con la lista de donaciones detalladas en formato MostrarDonacionDTO.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/historial-donaciones")
    public ResponseEntity<MensajeDTO<List<MostrarDonacionDTO>>> obtenerTodasLasDonaciones() {
        List<MostrarDonacionDTO> donaciones = donationService.obtenerTodasLasDonaciones();
        return ResponseEntity.ok(new MensajeDTO<>(false, donaciones));
    }
}