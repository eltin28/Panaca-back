package Panaca.service.service;

import Panaca.dto.evento.*;
import Panaca.model.documents.Evento;
import Panaca.exceptions.EventoException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EventoService {

    void crearEvento(CrearEventoDTO crearEventoDTO) throws EventoException;

    void editarEvento(String id, EditarEventoDTO editarEventoDTO) throws EventoException;

    void eliminarEvento(String id) throws EventoException;

    Evento obtenerInformacionEvento(String id) throws EventoException;

    List<ItemEventoDTO> listarEventos();

    Page<ItemEventoDTO> getEventoActivos(Pageable pageable);

    Page<ItemEventoDTO> getEventosInactivos(Pageable pageable);

    List<EventoFiltradoDTO> filtrarEventos(EventoFiltradoDTO filtro);

}
