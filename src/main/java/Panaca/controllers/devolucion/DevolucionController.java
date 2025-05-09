package Panaca.controllers.devolucion;

import Panaca.dto.devolucion.*;
import Panaca.service.devolucion.DevolucionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping
public class DevolucionController {

    private final DevolucionService service;

    public DevolucionController(DevolucionService service) {
        this.service = service;
    }

    // Solicitar devolución (cliente)
    @PostMapping("/api/devoluciones")
    public ResponseEntity<DevolucionResponseDTO> solicitar(@RequestBody DevolucionRequestDTO dto) {
        return ResponseEntity.ok(service.solicitar(dto));
    }

    // Historial de un usuario
    @GetMapping("/api/devoluciones")
    public ResponseEntity<List<DevolucionResponseDTO>> historial(@RequestParam String cuentaId) {
        return ResponseEntity.ok(service.listarPorUsuario(cuentaId));
    }

    // Listado completo (admin)
    @GetMapping("/api/admin/devoluciones")
    public ResponseEntity<List<DevolucionResponseDTO>> listarTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    // Aprobar y rechazar (admin)
    @PatchMapping("/api/admin/devoluciones/{id}/aprobar")
    public ResponseEntity<DevolucionResponseDTO> aprobar(@PathVariable Long id) {
        return ResponseEntity.ok(service.aprobar(id));
    }

    @PatchMapping("/api/admin/devoluciones/{id}/rechazar")
    public ResponseEntity<DevolucionResponseDTO> rechazar(@PathVariable Long id) {
        return ResponseEntity.ok(service.rechazar(id));
    }
}
