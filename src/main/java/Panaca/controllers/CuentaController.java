package Panaca.controllers;

import Panaca.dto.PQR.CrearPQRDTO;
import Panaca.dto.PQR.ItemPQRDTO;
import Panaca.dto.autenticacion.MensajeDTO;
import Panaca.dto.carrito.DetalleCarritoDTO;
import Panaca.dto.carrito.InformacionEventoCarritoDTO;
import Panaca.dto.cuenta.EditarCuentaDTO;
import Panaca.dto.cuenta.InformacionCuentaDTO;
import Panaca.dto.devolucion.DevolucionRequestDTO;
import Panaca.dto.devolucion.DevolucionResponseDTO;
import Panaca.dto.donation.DonationDTO;
import Panaca.dto.donation.MostrarDonacionDTO;
import Panaca.dto.orden.EditarOrdenDTO;
import Panaca.dto.orden.MostrarOrdenDTO;
import Panaca.exceptions.*;
import Panaca.model.documents.Carrito;
import Panaca.model.documents.PQR;
import Panaca.service.service.*;
import Panaca.configs.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import com.mercadopago.resources.preference.Preference;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cuenta")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class CuentaController {

    @Autowired
    CuentaService cuentaService;
    @Autowired
    PQRService PQRService;
    @Autowired
    CuponService cuponService;
    @Autowired
    CarritoService carritoService;
    @Autowired
    OrdenService ordenService;
    @Autowired
    DonationService donationService;
    @Autowired
    DevolucionService devolucionService;

//==================================== METODOS PERFIL =============================================//

    /**
     * Permite a un usuario con rol CLIENTE editar su perfil.
     *
     * @param cuenta DTO con los datos actualizados del usuario.
     * @return ResponseEntity con mensaje de éxito si la edición fue correcta.
     * @throws CuentaException si ocurre un error durante la edición de la cuenta.
     */
    @PreAuthorize("hasRole('CLIENTE')")
    @PutMapping("/editar-perfil")
    public ResponseEntity<MensajeDTO<String>> editarCuenta(@Valid @RequestBody EditarCuentaDTO cuenta) throws CuentaException {
        cuentaService.editarCuenta(cuenta);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Cuenta editada exitosamente"));
    }

    /**
     * Elimina la cuenta del usuario autenticado con rol CLIENTE.
     *
     * @param authentication Información del token JWT para obtener el ID del usuario.
     * @return ResponseEntity con mensaje de éxito si la cuenta fue eliminada.
     * @throws CuentaException si ocurre un error durante la eliminación.
     */
    @PreAuthorize("hasRole('CLIENTE')")
    @DeleteMapping("/eliminar-cuenta")
    public ResponseEntity<MensajeDTO<String>> eliminarCuenta(Authentication authentication) throws CuentaException {
        String id = AuthUtils.obtenerIdUsuarioDesdeToken(authentication);
        cuentaService.eliminarCuenta(id);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Cuenta eliminada exitosamente"));
    }

    /**
     * Obtiene la información del perfil del usuario autenticado con rol CLIENTE.
     *
     * @param authentication Información del token JWT para extraer el ID del usuario.
     * @return ResponseEntity con los datos del perfil encapsulados en InformacionCuentaDTO.
     * @throws CuentaException si no se encuentra la cuenta o hay un error al consultar.
     */
    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/obtener-informacion")
    public ResponseEntity<MensajeDTO<InformacionCuentaDTO>> obtenerInformacionCuenta(Authentication authentication) throws CuentaException {
        String id = AuthUtils.obtenerIdUsuarioDesdeToken(authentication);
        InformacionCuentaDTO info = cuentaService.obtenerInformacionCuenta(id);
        return ResponseEntity.ok(new MensajeDTO<>(false, info));
    }

//==================================== METODOS PQR =============================================//

    /**
     * Crea una nueva PQR asociada al usuario autenticado con rol CLIENTE.
     *
     * @param crearPQRDTO DTO con los datos de la solicitud PQR.
     * @return ResponseEntity con mensaje de éxito si se creó correctamente.
     * @throws PQRException si ocurre un error durante la creación de la PQR.
     */
    @PreAuthorize("hasRole('CLIENTE')")
    @PostMapping("/crear-pqr")
    public ResponseEntity<MensajeDTO<String>> crearPQR(@Valid @RequestBody CrearPQRDTO crearPQRDTO) throws PQRException {
        PQRService.crearPQR(crearPQRDTO);
        return ResponseEntity.ok(new MensajeDTO<>(true, "PQR creada exitosamente"));
    }

    /**
     * Lista todas las PQRs asociadas al usuario autenticado con rol CLIENTE.
     *
     * @return ResponseEntity con una lista de PQRs en formato ItemPQRDTO.
     * @throws PQRException si ocurre un error durante la consulta.
     */
    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/listar-pqr")
    public ResponseEntity<MensajeDTO<List<ItemPQRDTO>>> listarPQRs() throws PQRException {
        List<ItemPQRDTO> pqrList = PQRService.listarPQRs();
        return ResponseEntity.ok(new MensajeDTO<>(true, pqrList));
    }

    /**
     * Obtiene todas las PQRs asociadas al usuario autenticado con rol CLIENTE.
     *
     * @param authentication Información del token JWT para obtener el ID del usuario.
     * @return ResponseEntity con una lista de objetos PQR del usuario.
     * @throws PQRException si ocurre un error durante la consulta.
     */
    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/obtener-pqr-usuario")
    public ResponseEntity<MensajeDTO<List<PQR>>> obtenerPQRsPorUsuario(Authentication authentication) throws PQRException {
        String id = AuthUtils.obtenerIdUsuarioDesdeToken(authentication);
        List<PQR> pqrsObtenidos = PQRService.obtenerPQRsPorUsuario(id);
        return ResponseEntity.ok(new MensajeDTO<>(true, pqrsObtenidos));
    }

//==================================== METODOS CARRITO =============================================//

    /**
     * Agrega una lista de items al carrito del usuario autenticado con rol CLIENTE.
     *
     * @param authentication Token JWT para obtener el ID del usuario.
     * @param itemsCarritoDTO Lista de items a agregar al carrito.
     * @return ResponseEntity con mensaje de éxito si la operación fue exitosa.
     * @throws CarritoException si ocurre un error al agregar los items.
     */
    @PreAuthorize("hasRole('CLIENTE')")
    @PutMapping("/agregar-items-carrito")
    public ResponseEntity<MensajeDTO<String>> agregarItemsAlCarrito(Authentication authentication, @Valid @RequestBody List<DetalleCarritoDTO> itemsCarritoDTO) throws CarritoException {
        String idUsuario = AuthUtils.obtenerIdUsuarioDesdeToken(authentication);
        carritoService.agregarItemsAlCarrito(idUsuario, itemsCarritoDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Items agregados exitosamente al carrito."));
    }

    /**
     * Obtiene el carrito del usuario autenticado con rol CLIENTE.
     *
     * @param authentication Token JWT para extraer el ID del usuario.
     * @return ResponseEntity con el objeto Carrito asociado al usuario.
     * @throws CarritoException si no se encuentra o hay error en la recuperación del carrito.
     */
    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/obtener-carrito")
    public ResponseEntity<MensajeDTO<Carrito>> obtenerCarrito(Authentication authentication) throws CarritoException {
        String idUsuario = AuthUtils.obtenerIdUsuarioDesdeToken(authentication);
        Carrito carrito = carritoService.obtenerCarritoPorUsuario(idUsuario);
        return ResponseEntity.ok(new MensajeDTO<>(false, carrito));
    }

    /**
     * Elimina un ítem específico del carrito del usuario autenticado, según el ID del evento y la fecha de uso.
     *
     * @param authentication Token JWT para identificar al usuario.
     * @param idEvento ID del evento que se desea eliminar del carrito.
     * @param fechaUso Fecha de uso asociada al ítem a eliminar.
     * @return ResponseEntity con mensaje de éxito si el ítem fue eliminado.
     * @throws CarritoException si ocurre un error durante la eliminación.
     */
    @PreAuthorize("hasRole('CLIENTE')")
    @DeleteMapping("/eliminar-item-carrito/{idEvento}/{fechaUso}")
    public ResponseEntity<MensajeDTO<String>> eliminarItemDelCarrito(
            Authentication authentication,
            @PathVariable String idEvento,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaUso
    ) throws CarritoException {
        String idUsuario = AuthUtils.obtenerIdUsuarioDesdeToken(authentication);
        carritoService.eliminarItemDelCarrito(idUsuario, idEvento, fechaUso);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Item eliminado del carrito exitosamente."));
    }

    /**
     * Vacía por completo el carrito del usuario autenticado con rol CLIENTE.
     *
     * @param authentication Token JWT para identificar al usuario.
     * @return ResponseEntity con mensaje de confirmación si el vaciado fue exitoso.
     * @throws CarritoException si ocurre un error al vaciar el carrito.
     */
    @PreAuthorize("hasRole('CLIENTE')")
    @DeleteMapping("/vaciar-carrito")
    public ResponseEntity<MensajeDTO<String>> vaciarCarrito(Authentication authentication) throws CarritoException {
        String idUsuario = AuthUtils.obtenerIdUsuarioDesdeToken(authentication);
        carritoService.vaciarCarrito(idUsuario);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Carrito vaciado exitosamente."));
    }

    /**
     * Lista los productos detallados del carrito del usuario autenticado con rol CLIENTE.
     *
     * @param authentication Token JWT para identificar al usuario.
     * @return ResponseEntity con la lista de InformacionEventoCarritoDTO de los productos en el carrito.
     * @throws CarritoException si ocurre un error al obtener los productos.
     */
    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/listar-productos-carrito")
    public ResponseEntity<MensajeDTO<List<InformacionEventoCarritoDTO>>> listarProductosEnCarrito(Authentication authentication) throws CarritoException {
        String idUsuario = AuthUtils.obtenerIdUsuarioDesdeToken(authentication);
        List<InformacionEventoCarritoDTO> itemsCarrito = carritoService.listarProductosEnCarrito(idUsuario);
        return ResponseEntity.ok(new MensajeDTO<>(false, itemsCarrito));
    }

    /**
     * Calcula el valor total del carrito del usuario autenticado con rol CLIENTE.
     *
     * @param authentication Token JWT para obtener el ID del usuario.
     * @return ResponseEntity con el total calculado en formato Double.
     * @throws CarritoException si ocurre un error al calcular el total.
     */
    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/total-carrito")
    public ResponseEntity<MensajeDTO<Double>> calcularTotalCarrito(Authentication authentication) throws CarritoException {
        String idUsuario = AuthUtils.obtenerIdUsuarioDesdeToken(authentication);
        double total = carritoService.calcularTotalCarrito(idUsuario);
        return ResponseEntity.ok(new MensajeDTO<>(false, total));
    }

//==================================== METODOS ORDEN =============================================//

    /**
     * Muestra los detalles de una orden específica para el usuario autenticado con rol CLIENTE.
     *
     * @param idOrden ID de la orden a consultar.
     * @return ResponseEntity con los detalles de la orden en formato MostrarOrdenDTO.
     * @throws CuentaException si la cuenta no es válida.
     * @throws OrdenException si la orden no existe o hay error al obtenerla.
     */
    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/mostrar-orden/{idOrden}")
    public ResponseEntity<MensajeDTO<MostrarOrdenDTO>> mostrarOrden(@PathVariable String idOrden) throws CuentaException, OrdenException {
        MostrarOrdenDTO ordenDTO = ordenService.mostrarOrden(idOrden);
        return ResponseEntity.ok(new MensajeDTO<>(false, ordenDTO));
    }

    /**
     * Actualiza una orden existente para el usuario autenticado con rol CLIENTE.
     *
     * @param id ID de la orden a actualizar.
     * @param ordenDTO DTO con los nuevos datos de la orden, incluyendo código de cupón y detalles.
     * @return ResponseEntity con mensaje de éxito si la actualización fue realizada correctamente.
     * @throws OrdenException si ocurre un error al actualizar la orden.
     * @throws CuponException si el cupón es inválido o no puede aplicarse.
     */
    @PreAuthorize("hasRole('CLIENTE')")
    @PutMapping("/actualizar-orden/{id}")
    public ResponseEntity<MensajeDTO<String>> actualizarOrden(@PathVariable String id, @RequestBody @Valid EditarOrdenDTO ordenDTO) throws OrdenException, CuponException {
        ordenService.actualizarOrden(id, ordenDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Orden actualizada exitosamente."));
    }

    /**
     * Elimina una orden específica del usuario autenticado con rol CLIENTE.
     *
     * @param id ID de la orden a eliminar.
     * @return ResponseEntity con mensaje de confirmación si la eliminación fue exitosa.
     * @throws OrdenException si no se encuentra la orden o hay un error durante el proceso.
     */
    @PreAuthorize("hasRole('CLIENTE')")
    @DeleteMapping("/eliminar-orden/{id}")
    public ResponseEntity<MensajeDTO<String>> eliminarOrden(@PathVariable String id) throws OrdenException {
        ordenService.eliminarOrden(id);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Orden eliminada exitosamente."));
    }

//==================================== METODOS PAGO =============================================//

    /**
     * Inicia el proceso de pago para una orden mediante MercadoPago, generando una preferencia de pago.
     *
     * @param idOrden ID de la orden que se desea pagar.
     * @return ResponseEntity con la preferencia de pago generada por MercadoPago.
     * @throws Exception si ocurre un error durante la creación de la preferencia.
     */
    @PreAuthorize("hasRole('CLIENTE')")
    @PostMapping("/realizar-pago")
    public ResponseEntity<MensajeDTO<Preference>> realizarPago(@RequestParam("idOrden") String idOrden) throws Exception {
        Preference preference = ordenService.realizarPago(idOrden);
        return ResponseEntity.ok(new MensajeDTO<>(false, preference));
    }

    /**
     * Recibe notificaciones de pago desde MercadoPago (Webhook).
     * No requiere autenticación ya que es invocado externamente por MercadoPago.
     *
     * @param requestBody Mapa con la información de la notificación enviada por MercadoPago.
     */
    @PostMapping("/notificacion-pago")
    public void recibirNotificacionMercadoPago(@RequestBody Map<String, Object> requestBody) {
        ordenService.recibirNotificacionMercadoPago(requestBody);
    }

    /**
     * Crea una orden a partir del contenido del carrito del usuario autenticado.
     *
     * @param authentication Token JWT para identificar al usuario.
     * @param idCupon (opcional) ID del cupón a aplicar.
     * @param codigoPasarela (opcional) Código de la pasarela de pago (si aplica).
     * @return ResponseEntity con mensaje de éxito o error según el resultado de la operación.
     */
    @PreAuthorize("hasRole('CLIENTE')")
    @PostMapping("/crear-orden")
    public ResponseEntity<MensajeDTO<String>> crearOrdenDesdeCarrito(
            Authentication authentication,
            @RequestParam(required = false) String idCupon,
            @RequestParam(required = false) String codigoPasarela
    ) {
        try {
            String idCliente = AuthUtils.obtenerIdUsuarioDesdeToken(authentication);
            ordenService.crearOrdenDesdeCarrito(idCliente, idCupon, codigoPasarela);
            return ResponseEntity.ok(new MensajeDTO<>(false, "Orden creada exitosamente desde el carrito."));
        } catch (OrdenException | CarritoException | CuponException e) {
            return ResponseEntity.badRequest().body(new MensajeDTO<>(false, e.getMessage()));
        }
    }

    //==================================== METODOS DONACIONES =============================================//

    /**
     * Registra una nueva donación por parte del usuario autenticado con rol CLIENTE.
     *
     * @param dto DTO con la información de la donación.
     * @return ResponseEntity con mensaje de confirmación si la donación fue registrada correctamente.
     */
    @PreAuthorize("hasRole('CLIENTE')")
    @PostMapping("/donaciones-crear")
    public ResponseEntity<MensajeDTO<String>> donar(@Valid @RequestBody DonationDTO dto) {
        donationService.crearDonacion(dto);
        return ResponseEntity.ok(new MensajeDTO<>(true, "Donación registrada exitosamente"));
    }

    /**
     * Genera una preferencia de pago en MercadoPago para una donación registrada.
     *
     * @param idDonacion ID de la donación.
     * @return ResponseEntity con la preferencia de pago generada.
     * @throws Exception si ocurre un error durante la creación de la preferencia.
     */
    @PostMapping("/realizar-pago-donacion")
    public ResponseEntity<MensajeDTO<Preference>> realizarPagoDonacion(@RequestParam String idDonacion) throws Exception {
        Preference preference = donationService.realizarPagoDonacion(idDonacion);
        return ResponseEntity.ok(new MensajeDTO<>(false, preference));
    }

    /**
     * Obtiene el historial de donaciones realizadas por el usuario autenticado.
     *
     * @param authentication Información del token JWT.
     * @return ResponseEntity con la lista de donaciones en formato MostrarDonacionDTO.
     */
    @GetMapping("/historial-donaciones")
    public ResponseEntity<MensajeDTO<List<MostrarDonacionDTO>>> obtenerHistorialDonaciones(Authentication authentication) {
        String idCuenta = AuthUtils.obtenerIdUsuarioDesdeToken(authentication);
        List<MostrarDonacionDTO> historial = donationService.obtenerHistorialDonaciones(idCuenta);
        return ResponseEntity.ok(new MensajeDTO<>(false, historial));
    }

    //==================================== METODOS DEVOLUCIONES =============================================//

    /**
     * Solicita una devolución por parte del usuario autenticado con rol CLIENTE.
     *
     * @param dto DTO con la información necesaria para solicitar la devolución.
     * @return ResponseEntity con los detalles de la devolución registrada.
     */
    @PreAuthorize("hasRole('CLIENTE')")
    @PostMapping("/devoluciones-solicitar")
    public ResponseEntity<MensajeDTO<DevolucionResponseDTO>> solicitarDevolucion(
            @Valid @RequestBody DevolucionRequestDTO dto) {
        DevolucionResponseDTO response = devolucionService.solicitar(dto);
        return ResponseEntity.ok(new MensajeDTO<>(true, response));
    }

    /**
     * Lista el historial de devoluciones realizadas por el usuario autenticado con rol CLIENTE.
     *
     * @param authentication Token JWT para identificar al usuario.
     * @return ResponseEntity con la lista de devoluciones del usuario.
     */
    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/devoluciones-historial")
    public ResponseEntity<MensajeDTO<List<DevolucionResponseDTO>>> listarDevolucionesUsuario(
            Authentication authentication) {
        String cuentaId = AuthUtils.obtenerIdUsuarioDesdeToken(authentication);
        List<DevolucionResponseDTO> devoluciones = devolucionService.listarPorUsuario(cuentaId);
        return ResponseEntity.ok(new MensajeDTO<>(true, devoluciones));
    }
}