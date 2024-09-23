package co.edu.uniquindio.unieventos.service.service;

import co.edu.uniquindio.unieventos.dto.evento.*;
import co.edu.uniquindio.unieventos.exceptions.EventoException;
import co.edu.uniquindio.unieventos.model.vo.Localidad;

import java.util.List;

public interface EventoService {

    String crearEvento(CrearEventoDTO crearEventoDTO) throws EventoException;

    String editarEvento(EditarEventoDTO editarEventoDTO) throws EventoException;

    String eliminarEvento(String id) throws EventoException;

    InformacionEventoDTO obtenerInformacionEvento(String id) throws EventoException;

    List<ItemEventoDTO> listarEventos();

    List<ItemEventoDTO> filtrarEventos(FiltroEventoDTO filtroEventoDTO);
}
