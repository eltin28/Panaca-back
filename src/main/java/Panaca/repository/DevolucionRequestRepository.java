package Panaca.repository;

import Panaca.model.documents.DevolucionRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DevolucionRequestRepository extends MongoRepository<DevolucionRequest, String> {
    List<DevolucionRequest> findByIdCuenta(String cuentaId);
}
