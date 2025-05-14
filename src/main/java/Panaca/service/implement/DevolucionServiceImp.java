package Panaca.service.implement;

import Panaca.dto.devolucion.DevolucionRequestDTO;
import Panaca.dto.devolucion.DevolucionResponseDTO;
import Panaca.model.documents.*;
import Panaca.model.enums.EstadoDevolucion;
import Panaca.model.enums.TipoDevolucion;
import Panaca.repository.*;
import Panaca.service.service.DevolucionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Validated

public class DevolucionServiceImp implements DevolucionService {

    private final DevolucionRequestRepository devolucionRepo;
    private final CuentaRepository cuentaRepo;
    private final OrdenRepository ordenRepo;
    private final DonationRepository donationRepo;

    @Override
    public DevolucionResponseDTO solicitar(DevolucionRequestDTO dto) {
        Cuenta cuenta = cuentaRepo.findById(dto.cuentaId())
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        DevolucionRequest request = new DevolucionRequest();
        request.setIdCuenta(cuenta.getId());
        request.setTipo(dto.tipo());
        request.setReferenciaId(dto.referenciaId());
        request.setFechaSolicitud(LocalDateTime.now());
        request.setEstado(EstadoDevolucion.PENDIENTE);

        if (dto.tipo() == TipoDevolucion.TICKET) {
            Orden orden = ordenRepo.findById(dto.referenciaId())
                    .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
            LocalDate fechaUso = orden.getDetalle().get(0).getFechaUso();

            if (LocalDate.now().isAfter(fechaUso.minusWeeks(1))) {
                throw new RuntimeException("Ya no es posible solicitar devolución de ticket");
            }

        } else {
            Donation donation = donationRepo.findById(dto.referenciaId())
                    .orElseThrow(() -> new RuntimeException("Donación no encontrada"));

            if (LocalDateTime.now().isAfter(donation.getFecha().plusHours(24))) {
                throw new RuntimeException("Pasaron más de 24h para solicitar devolución de donación");
            }
        }

        DevolucionRequest saved = devolucionRepo.save(request);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DevolucionResponseDTO> listarPorUsuario(String cuentaId) {
        return devolucionRepo.findByIdCuenta(cuentaId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DevolucionResponseDTO> listarTodas() {
        return devolucionRepo.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public DevolucionResponseDTO aprobar(String id) {
        DevolucionRequest request = devolucionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        request.setEstado(EstadoDevolucion.APROBADA);
        return toResponse(devolucionRepo.save(request));
    }

    @Override
    public DevolucionResponseDTO rechazar(String id) {
        DevolucionRequest request = devolucionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        request.setEstado(EstadoDevolucion.RECHAZADA);
        return toResponse(devolucionRepo.save(request));
    }

    private DevolucionResponseDTO toResponse(DevolucionRequest r) {
        return new DevolucionResponseDTO(
                r.getId(),
                r.getTipo(),
                r.getReferenciaId(),
                r.getFechaSolicitud(),
                r.getEstado()
        );
    }
}
