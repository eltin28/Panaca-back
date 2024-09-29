package co.edu.uniquindio.unieventos.service.service;

import co.edu.uniquindio.unieventos.dto.evento.*;
import co.edu.uniquindio.unieventos.exceptions.EventoException;
import co.edu.uniquindio.unieventos.model.documents.Evento;
import co.edu.uniquindio.unieventos.model.enums.TipoEvento;
import co.edu.uniquindio.unieventos.model.vo.Localidad;
import jdk.jfr.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventoService {

    String crearEvento(CrearEventoDTO crearEventoDTO) throws EventoException;

    List<Localidad> crearLocalidades(List<LocalidadDTO> listaLocalidadesDTO);

    String editarEvento(EditarEventoDTO editarEventoDTO) throws EventoException;

    List<Localidad> modificarLocalidades(List<Localidad> localidadesActuales ,List<LocalidadDTO> listaLocalidadesDTO) throws EventoException;

    String eliminarEvento(String id) throws EventoException;

    InformacionEventoDTO obtenerInformacionEvento(String id) throws EventoException;

    List<ItemEventoDTO> listarEventos();

    List<Evento> filtrarPorTipo(TipoEvento tipoEvento);

    List<Evento> filtrarPorFecha(LocalDateTime fecha);

    List<Evento> filtrarPorCiudad(String ciudad);

    List<Evento> filtrarPorRangoDeFechas(LocalDateTime desde, LocalDateTime hasta);

    //List<ItemEventoDTO> filtrarEventos(FiltroEventoDTO filtroEventoDTO);

}
