package co.edu.uniquindio.unieventos;

import co.edu.uniquindio.unieventos.model.documents.Cuenta;
import co.edu.uniquindio.unieventos.model.enums.EstadoCuenta;
import co.edu.uniquindio.unieventos.model.enums.Rol;
import co.edu.uniquindio.unieventos.model.vo.Usuario;
import co.edu.uniquindio.unieventos.repository.CuentaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
public class CuentaTest {

    @Autowired
    private CuentaRepository cuentaRepo;

    @Test
    public void registrarTest() {
        //Creamos el usuario con sus propiedades
        Cuenta cuenta = Cuenta.builder()
                .usuario(new Usuario("Pepito perez","1213444","3012223333","Calle 12 # 12-12"))
                .email("pepitoperez@gmail.com")
                .password("123456")
                .fechaRegistro(LocalDateTime.now())
                .rol(Rol.CLIENTE)
                .estado(EstadoCuenta.ACTIVO)
                .build();


        //Guardamos el usuario en la base de datos
        Cuenta registro = cuentaRepo.save(cuenta);


        //Verificamos que se haya guardado validando que no sea null
        Assertions.assertNotNull(registro);
    }

    @Test
    public void actualizarTest(){
        //Obtenemos el usuario con el id XXXXXXX
        Cuenta cuenta = cuentaRepo.findById("XXXXXXX").orElseThrow();
        //Modificar el email del cliente
        cuenta.setEmail("hola@gmail.com");

        //Guardamos el cliente
        cuentaRepo.save( cuenta );

        //Obtenemos el usuario con el id XXXXXXX nuevamente
        Cuenta cuentaActualizada = cuentaRepo.findById("XXXXXXX").orElseThrow();;


        //Verificamos que el email se haya actualizado
        Assertions.assertEquals("hola@gmail.com", cuentaActualizada.getEmail());
    }

    @Test
    public void listarTodosTest(){
        //Obtenemos la lista de todos los usuarios (por ahora solo tenemos 1)
        List<Cuenta> cuentas = cuentaRepo.findAll();

        //Imprimimos los usuarios, se hace uso de una función lambda
        cuentas.forEach(System.out::println);

        //Verificamos que solo exista un usuario
        Assertions.assertEquals(1, cuentas.size());
    }

    @Test
    public void eliminarTest(){
        //Borramos el usuario con el id XXXXXXX
        cuentaRepo.deleteById("XXXXXXX");

        //Obtenemos el usuario con el id XXXXXXX
        Cuenta cuenta = cuentaRepo.findById("XXXXXXX").orElse(null);

        //Verificamos que el usuario no exista (sea null) ya que fue eliminado
        Assertions.assertNull(cuenta);
    }


}
