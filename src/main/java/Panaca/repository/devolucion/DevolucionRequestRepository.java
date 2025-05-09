package Panaca.repository.devolucion;

import Panaca.model.devolucion.DevolucionRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DevolucionRequestRepository extends JpaRepository<DevolucionRequest, Long> {
    List<DevolucionRequest> findByCuentaId(String cuentaId);
}

