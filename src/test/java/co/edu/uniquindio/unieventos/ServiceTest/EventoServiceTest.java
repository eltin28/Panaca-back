package co.edu.uniquindio.unieventos.ServiceTest;

import co.edu.uniquindio.unieventos.dto.evento.CrearEventoDTO;
import co.edu.uniquindio.unieventos.dto.evento.LocalidadDTO;
import co.edu.uniquindio.unieventos.exceptions.EventoException;
import co.edu.uniquindio.unieventos.model.enums.EstadoEvento;
import co.edu.uniquindio.unieventos.model.enums.TipoEvento;
import co.edu.uniquindio.unieventos.repository.EventoRepository;
import co.edu.uniquindio.unieventos.service.service.EventoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class EventoServiceTest {

    @Autowired
    private EventoRepository eventoRepository;
    @Autowired
    private EventoService eventoService;

    @Test
    public void CrearEventoTest() throws EventoException {

        List<LocalidadDTO> listaLocalidades = new ArrayList<>();
            listaLocalidades.add(new LocalidadDTO("Platea", 100, 50.000d));
            listaLocalidades.add(new LocalidadDTO("General", 200, 200.000d));
            listaLocalidades.add(new LocalidadDTO("VIP", 50, 100.000d));


        CrearEventoDTO crearEventoDTO = new CrearEventoDTO(
                "url-imagenPortada",
                "Evento de rock",
                "Concierto de rock en vivo",
                "Av. Principal 123",
                "url-imagenLocalidades",
                TipoEvento.CONCIERTO,
                EstadoEvento.ACTIVO,
                LocalDateTime.now(),
                "Ciudad de ejemplo",
                listaLocalidades
        );

        System.out.println(crearEventoDTO.listaLocalidades());
        String resultado = eventoService.crearEvento(crearEventoDTO);

        assertNotNull(resultado);
        assertTrue(resultado.startsWith("Evento creado con éxito. ID: "));
    }

}
