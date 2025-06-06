package Panaca.controllers;

import Panaca.dto.PQR.CrearPQRDTO;
import Panaca.dto.PQR.ItemPQRDTO;
import Panaca.dto.autenticacion.MensajeDTO;
import Panaca.dto.carrito.DetalleCarritoDTO;
import Panaca.dto.carrito.InformacionEventoCarritoDTO;
import Panaca.dto.cuenta.EditarCuentaDTO;
import Panaca.dto.cuenta.InformacionCuentaDTO;
import Panaca.dto.orden.EditarOrdenDTO;
import Panaca.dto.orden.MostrarOrdenDTO;
import Panaca.exceptions.*;
import Panaca.model.documents.Carrito;
import Panaca.model.documents.PQR;
import Panaca.service.service.*;
import Panaca.dto.cuenta.*;
import Panaca.exceptions.*;
import Panaca.service.service.*;
import com.mercadopago.resources.preference.Preference;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    Panaca.service.service.PQRService PQRService;
    @Autowired
    CuponService cuponService;
    @Autowired
    CarritoService carritoService;
    @Autowired
    OrdenService ordenService;

    @PutMapping("/editar-perfil")
    public ResponseEntity<MensajeDTO<String>> editarCuenta(@Valid @RequestBody EditarCuentaDTO cuenta) throws CuentaException {
        try {
            cuentaService.editarCuenta(cuenta);
            return ResponseEntity.ok(new MensajeDTO<>(false, "Cuenta editada exitosamente"));
        }catch (CuentaException e){
            return ResponseEntity.badRequest().body(new MensajeDTO<>(false, e.getMessage()));
        }
    }

    @DeleteMapping("/eliminar-cuenta/{id}")
    public ResponseEntity<MensajeDTO<String>> eliminarCuenta(@Valid @PathVariable String id) throws CuentaException {
        try {
            cuentaService.eliminarCuenta(id);
            return ResponseEntity.ok(new MensajeDTO<>(false, "Cuenta eliminada exitosamente"));
        }catch (CuentaException e){
            return ResponseEntity.badRequest().body(new MensajeDTO<>(false, e.getMessage()));
        }
    }

    @GetMapping("/obtener-informacion/{id}")
    public ResponseEntity<MensajeDTO<InformacionCuentaDTO>> obtenerInformacionCuenta(@Valid @PathVariable String id) throws CuentaException{
        InformacionCuentaDTO info = cuentaService.obtenerInformacionCuenta(id);
        return ResponseEntity.ok(new MensajeDTO<>(false, info));
    }


    //==================================== METODOS PQR =============================================//

    /**
     * Método para crear una nueva PQR.
     * @param crearPQRDTO Datos de la PQR a crear.
     * @return La PQR creada.
     */
    @PostMapping("/crear-pqr")
    public ResponseEntity<MensajeDTO<String>> crearPQR(@Valid @RequestBody CrearPQRDTO crearPQRDTO) {
        try {
            PQRService.crearPQR(crearPQRDTO);
            return ResponseEntity.ok(new MensajeDTO<>(true,"PQR creada exitosamente"));
        } catch (PQRException e) {
            return ResponseEntity.badRequest().body(new MensajeDTO<>(false,e.getMessage()));
        }
    }

    /**
     * Método para listar todas las PQRs.
     * @return Lista de PQRs.
     */
    @GetMapping("/listar-pqr")
    public ResponseEntity<MensajeDTO<List<ItemPQRDTO>>> listarPQRs() throws PQRException {
        List<ItemPQRDTO> pqrList = PQRService.listarPQRs();
        return ResponseEntity.badRequest().body(new MensajeDTO<>(true,pqrList));
    }

    @GetMapping("/obtener-pqr-usuario/{id}")
    public ResponseEntity<MensajeDTO<List<PQR>>> obtenerPQRsPorUsuario(@Valid @PathVariable String id) throws PQRException {
        List<PQR> pqrsObtenidos = PQRService.obtenerPQRsPorUsuario(id);
        return ResponseEntity.ok(new MensajeDTO<>(true, pqrsObtenidos));
    }

    //==================================== METODOS CARRITO =============================================//

    @PutMapping("/agregar-items-carrito/{idUsuario}")
    public ResponseEntity<MensajeDTO<String>> agregarItemsAlCarrito(@PathVariable String idUsuario, @Valid @RequestBody List<DetalleCarritoDTO> itemsCarritoDTO) throws CarritoException {
        carritoService.agregarItemsAlCarrito(idUsuario, itemsCarritoDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Items agregados exitosamente al carrito."));
    }

    @GetMapping("/obtener-carrito-idUsuario/{idUsuario}")
    public ResponseEntity<MensajeDTO<Carrito>> obtenerCarrito(@PathVariable String idUsuario) throws CarritoException {
        Carrito carrito = carritoService.obtenerCarritoPorUsuario(idUsuario);
        return ResponseEntity.ok(new MensajeDTO<>(false, carrito));
    }

    @DeleteMapping("/eliminar-item-carrito/{idUsuario}/{nombreLocalidad}")
    public ResponseEntity<MensajeDTO<String>> eliminarItemDelCarrito(@PathVariable String idUsuario, @PathVariable String nombreLocalidad) throws CarritoException {
        carritoService.eliminarItemDelCarrito(idUsuario, nombreLocalidad);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Item eliminado del carrito exitosamente."));
    }

    @DeleteMapping("/vaciar-carrito/{idUsuario}")
    public ResponseEntity<MensajeDTO<String>> vaciarCarrito(@PathVariable String idUsuario) throws CarritoException {
        carritoService.vaciarCarrito(idUsuario);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Carrito vaciado exitosamente."));
    }

    @GetMapping("/listar-productos-carrito/{idUsuario}")
    public ResponseEntity<MensajeDTO<List<InformacionEventoCarritoDTO>>> listarProductosEnCarrito(@PathVariable String idUsuario) throws CarritoException {
        List<InformacionEventoCarritoDTO> itemsCarrito = carritoService.listarProductosEnCarrito(idUsuario);
        return ResponseEntity.ok(new MensajeDTO<>(false, itemsCarrito));
    }

    // Método para calcular el total del carrito
    @GetMapping("/total-carrito/{idUsuario}")
    public ResponseEntity<MensajeDTO<Double>> calcularTotalCarrito(@PathVariable String idUsuario) throws CarritoException {
        double total = carritoService.calcularTotalCarrito(idUsuario);
        return ResponseEntity.ok(new MensajeDTO<>(false, total));
    }

    // Método para validar la disponibilidad de entradas
    @GetMapping("/validar-disponibilidad/{idUsuario}")
    public ResponseEntity<MensajeDTO<Boolean>> validarDisponibilidadEntradas(@PathVariable String idUsuario) throws CarritoException {
        boolean disponible = carritoService.validarDisponibilidadEntradas(idUsuario);
        return ResponseEntity.ok(new MensajeDTO<>(false, disponible));
    }

    //==================================== METODOS ORDEN =============================================//

    /*
    @PostMapping("/crear-orden")
    public ResponseEntity<MensajeDTO<String>> crearOrden(@Valid @RequestBody CrearOrdenDTO ordenDTO, @Valid @RequestBody double totalOrden) throws OrdenException {
        try {
            ordenService.crearOrden(ordenDTO, totalOrden);
            return ResponseEntity.ok(new MensajeDTO<>(false, "Orden creada exitosamente"));
        }catch (OrdenException e){
            return ResponseEntity.badRequest().body(new MensajeDTO<>(false, e.getMessage()));
        }
    }

    */

    @GetMapping("/mostrar-orden/{idOrden}")
    public ResponseEntity<MensajeDTO<MostrarOrdenDTO>> mostrarOrden(@PathVariable String idOrden) throws CuentaException, OrdenException {
            MostrarOrdenDTO ordenDTO = ordenService.mostrarOrden(idOrden);
            return ResponseEntity.ok(new MensajeDTO<>(false, ordenDTO));
    }
    @PutMapping("/actualizar-orden/{id}")
    public ResponseEntity<MensajeDTO<String>> actualizarOrden(@Valid @PathVariable String id, @Valid @RequestBody EditarOrdenDTO ordenDTO) throws OrdenException {
        try {
            ordenService.actualizarOrden(id, ordenDTO);
            return ResponseEntity.ok(new MensajeDTO<>(false, "Orden actualizada exitosamente."));
        }catch (OrdenException e){
            return ResponseEntity.badRequest().body(new MensajeDTO<>(false, e.getMessage()));
        }
    }

    @DeleteMapping("/eliminar-orden/{id}")
    public ResponseEntity<MensajeDTO<String>> eliminarOrden(@Valid @PathVariable String id) throws OrdenException {
        try {
            ordenService.eliminarOrden(id);
            return ResponseEntity.ok(new MensajeDTO<>(false, "Orden eliminada exitosamente."));
        }catch (OrdenException e){
            return ResponseEntity.badRequest().body(new MensajeDTO<>(false, e.getMessage()));
        }
    }

    @PostMapping("/realizar-pago")
    public ResponseEntity<MensajeDTO<Preference>> realizarPago(@RequestParam("idOrden") String idOrden) throws Exception{
        return ResponseEntity.ok().body(new MensajeDTO<>(false, ordenService.realizarPago(idOrden)));
    }

    @PostMapping("/notificacion-pago")
    public void recibirNotificacionMercadoPago(@RequestBody Map<String, Object> requestBody) {
        ordenService.recibirNotificacionMercadoPago(requestBody);
    }

    @PostMapping("/crear-orden")
    public ResponseEntity<MensajeDTO<String>> crearOrdenDesdeCarrito(@RequestParam String idCliente,
                                                    @RequestParam(required = false) String idCupon,
                                                    @RequestParam(required = false) String codigoPasarela) {
        try {
            ordenService.crearOrdenDesdeCarrito(idCliente, idCupon, codigoPasarela);
            return ResponseEntity.ok(new MensajeDTO<>(false, "Orden desde carrito exitosamente."));
        } catch (OrdenException | CarritoException | CuponException e) {
            return ResponseEntity.badRequest().body(new MensajeDTO<>(false, e.getMessage()));
        }
    }

}
