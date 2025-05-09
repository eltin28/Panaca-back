package Panaca.service.devolucion;

import Panaca.dto.devolucion.DevolucionRequestDTO;
import Panaca.dto.devolucion.DevolucionResponseDTO;
import Panaca.model.devolucion.DevolucionRequest;
import Panaca.model.devolucion.EstadoDevolucion;
import Panaca.model.devolucion.TipoDevolucion;
import Panaca.repository.devolucion.DevolucionRequestRepository;
import Panaca.model.documents.Cuenta;
import Panaca.repository.CuentaRepository;
import Panaca.model.documents.Orden;
import Panaca.repository.OrdenRepository;
import Panaca.model.documents.Evento;
import Panaca.repository.EventoRepository;
import Panaca.model.donation.Donation;
import Panaca.repository.DonationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DevolucionService {

    private final DevolucionRequestRepository repo;
    private final CuentaRepository cuentaRepo;
    private final OrdenRepository ordenRepo;
    private final EventoRepository eventoRepo;
    private final DonationRepository donationRepo;

    public DevolucionService(DevolucionRequestRepository repo,
                             CuentaRepository cuentaRepo,
                             OrdenRepository ordenRepo,
                             EventoRepository eventoRepo,
                             DonationRepository donationRepo) {
        this.repo = repo;
        this.cuentaRepo = cuentaRepo;
        this.ordenRepo = ordenRepo;
        this.eventoRepo = eventoRepo;
        this.donationRepo = donationRepo;
    }

    @Transactional
    public DevolucionResponseDTO solicitar(DevolucionRequestDTO dto) {
        // 1) Recuperar la Cuenta por String
        Cuenta cuenta = cuentaRepo.findById(dto.getCuentaId())
            .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        // 2) Crear solicitud
        DevolucionRequest req = new DevolucionRequest();
        req.setCuenta(cuenta);
        req.setTipo(dto.getTipo());
        req.setReferenciaId(dto.getReferenciaId());
        req.setFechaSolicitud(LocalDateTime.now());
        req.setEstado(EstadoDevolucion.PENDIENTE);

        // 3) Validar ventana de tiempo
        if (dto.getTipo() == TipoDevolucion.TICKET) {
            Orden orden = ordenRepo.findById(dto.getReferenciaId())
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

            LocalDate fechaUso = orden.getDetalle().get(0).getFechaUso();
            if (LocalDate.now().isAfter(fechaUso.minusWeeks(1))) {
                throw new RuntimeException("Ya no es posible solicitar devolución de ticket");
            }

        } else { // DONACION
            Long donationId = Long.valueOf(dto.getReferenciaId());
            Donation donation = donationRepo.findById(donationId)
                .orElseThrow(() -> new RuntimeException("Donación no encontrada"));

            if (LocalDateTime.now().isAfter(donation.getFecha().plusHours(24))) {
                throw new RuntimeException("Pasaron más de 24h para solicitar devolución de donación");
            }
        }

        // 4) Guardar y devolver
        DevolucionRequest saved = repo.save(req);
        return new DevolucionResponseDTO(
            saved.getId(),
            saved.getTipo(),
            saved.getReferenciaId(),
            saved.getFechaSolicitud(),
            saved.getEstado()
        );
    }

    @Transactional(readOnly = true)
    public List<DevolucionResponseDTO> listarPorUsuario(String cuentaId) {
        return repo.findByCuentaId(cuentaId).stream()
            .map(r -> new DevolucionResponseDTO(
                r.getId(), r.getTipo(), r.getReferenciaId(),
                r.getFechaSolicitud(), r.getEstado()
            ))
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DevolucionResponseDTO> listarTodas() {
        return repo.findAll().stream()
            .map(r -> new DevolucionResponseDTO(
                r.getId(), r.getTipo(), r.getReferenciaId(),
                r.getFechaSolicitud(), r.getEstado()
            ))
            .collect(Collectors.toList());
    }

    @Transactional
    public DevolucionResponseDTO aprobar(Long id) {
        DevolucionRequest r = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        r.setEstado(EstadoDevolucion.APROBADA);
        DevolucionRequest updated = repo.save(r);
        return new DevolucionResponseDTO(
            updated.getId(), updated.getTipo(), updated.getReferenciaId(),
            updated.getFechaSolicitud(), updated.getEstado()
        );
    }

    @Transactional
    public DevolucionResponseDTO rechazar(Long id) {
        DevolucionRequest r = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        r.setEstado(EstadoDevolucion.RECHAZADA);
        DevolucionRequest updated = repo.save(r);
        return new DevolucionResponseDTO(
            updated.getId(), updated.getTipo(), updated.getReferenciaId(),
            updated.getFechaSolicitud(), updated.getEstado()
        );
    }
}

