package co.edu.uniquindio.unieventos.repository;

import co.edu.uniquindio.unieventos.model.documents.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends MongoRepository<Usuario, String>{

}
