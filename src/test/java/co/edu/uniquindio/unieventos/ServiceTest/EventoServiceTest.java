package co.edu.uniquindio.unieventos.ServiceTest;

import co.edu.uniquindio.unieventos.dto.evento.CrearEventoDTO;
import co.edu.uniquindio.unieventos.dto.evento.CrearLocalidadDTO;
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

        List<CrearLocalidadDTO> listaLocalidades = new ArrayList<>();
            listaLocalidades.add(new CrearLocalidadDTO("Platea", 100, 50.000d));
            listaLocalidades.add(new CrearLocalidadDTO("General", 200, 200.000d));
            listaLocalidades.add(new CrearLocalidadDTO("VIP", 50, 100.000d));


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
        eventoService.crearEvento(crearEventoDTO);

        assertNotNull(crearEventoDTO);
    }

}
