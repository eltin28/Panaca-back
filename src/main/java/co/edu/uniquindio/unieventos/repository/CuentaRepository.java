package co.edu.uniquindio.unieventos.repository;

import co.edu.uniquindio.unieventos.model.documents.Cuenta;
import co.edu.uniquindio.unieventos.model.vo.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CuentaRepository extends MongoRepository<Cuenta, String>{

    boolean existsByEmail(String correo);
    Optional<Cuenta> findByEmail(String correo);

    @Query("{'cedula': ?0}")
    Optional<Cuenta> findByCedula(String cedula);

    @Query("{'codigoValidacionRegistro': ?0}")
    Optional<Cuenta> existByCodigoValidacionRegistro(String codigoValidacionRegistro);

}
