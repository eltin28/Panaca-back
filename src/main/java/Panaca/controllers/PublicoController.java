package Panaca.controllers;

import Panaca.dto.autenticacion.MensajeDTO;
import Panaca.dto.evento.EventoFiltradoDTO;
import Panaca.dto.evento.ItemEventoDTO;
import Panaca.exceptions.EventoException;
import Panaca.model.documents.Evento;
import Panaca.service.service.CuentaService;
import Panaca.service.service.CuponService;
import Panaca.service.service.EmailService;
import Panaca.service.service.EventoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/publico")
@RequiredArgsConstructor
public class PublicoController {

    @Autowired
    CuentaService cuentaService;
    @Autowired
    EmailService emailService;
    @Autowired
    CuponService cuponService;
    @Autowired
    EventoService eventoService;

    //==================================== METODOS EVENTO =============================================//

    /**
     * Filtra los eventos públicos según nombre, tipo o estado.
     *
     * @param filtro DTO con los criterios de filtrado (nombre, tipo, estado).
     * @return ResponseEntity con la lista de eventos que cumplen con los filtros.
     * @throws EventoException si ocurre un error al filtrar los eventos.
     */
    @PostMapping("/filtrar-eventos")
    public ResponseEntity<MensajeDTO<List<EventoFiltradoDTO>>> filtrarEventos(@RequestBody EventoFiltradoDTO filtro) throws EventoException {
        List<EventoFiltradoDTO> eventosFiltrados = eventoService.filtrarEventos(filtro);
        return ResponseEntity.ok(new MensajeDTO<>(true, eventosFiltrados));
    }

    /**
     * Lista todos los eventos públicos disponibles.
     *
     * @return ResponseEntity con la lista de eventos representados como ItemEventoDTO.
     * @throws EventoException si ocurre un error durante la consulta.
     */
    @GetMapping("/listar-eventos")
    public ResponseEntity<MensajeDTO<List<ItemEventoDTO>>> listarEventos() throws EventoException {
        List<ItemEventoDTO> eventos = eventoService.listarEventos();
        return ResponseEntity.ok(new MensajeDTO<>(false, eventos));
    }

    /**
     * Obtiene la información detallada de un evento público por su ID.
     *
     * @param id Identificador del evento.
     * @return ResponseEntity con el objeto Evento correspondiente.
     * @throws EventoException si no se encuentra el evento o hay un error en la consulta.
     */
    @GetMapping("/obtener-evento/{id}")
    public ResponseEntity<Evento> obtenerInfoEvento(@Valid @PathVariable("id") String id) throws EventoException {
        return ResponseEntity.ok(eventoService.obtenerInformacionEvento(id));
    }
}