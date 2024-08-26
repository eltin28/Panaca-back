package co.edu.uniquindio.unieventos.model.documents;


import co.edu.uniquindio.unieventos.model.enums.EstadoCuenta;
import co.edu.uniquindio.unieventos.model.enums.Rol;
import jakarta.validation.GroupSequence;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document("Cuenta")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Cuenta {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    @Id
    private ObjectId idUsuario;

    private String email;
    private String password;
    private String codigoVerificacion;
    private String codigoVerificacionContrasenia;
    private LocalDate fechaRegistro;
    private Rol rol;
    EstadoCuenta estado;

}
