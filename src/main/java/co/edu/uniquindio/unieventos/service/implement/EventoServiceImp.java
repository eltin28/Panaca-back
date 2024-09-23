package co.edu.uniquindio.unieventos.service.implement;

import co.edu.uniquindio.unieventos.dto.evento.*;
import co.edu.uniquindio.unieventos.exceptions.EventoException;
import co.edu.uniquindio.unieventos.model.documents.Evento;
import co.edu.uniquindio.unieventos.model.vo.Localidad;
import co.edu.uniquindio.unieventos.repository.EventoRepository;
import co.edu.uniquindio.unieventos.service.service.EventoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class EventoServiceImp implements EventoService {

    private final EventoRepository eventoRepository;

    @Override
    public String crearEvento(CrearEventoDTO crearEventoDTO) throws EventoException {
        // Crear una nueva instancia de Evento
        Evento nuevoEvento = new Evento();
        nuevoEvento.setImagenPortada(crearEventoDTO.imagenPortada());
        nuevoEvento.setNombre(crearEventoDTO.nombre());
        nuevoEvento.setDescripcion(crearEventoDTO.descripcion());
        nuevoEvento.setDireccion(crearEventoDTO.direccion());
        nuevoEvento.setImagenesLocalidades(crearEventoDTO.imagenesLocalidades());
        nuevoEvento.setTipo(crearEventoDTO.tipoEvento());
        nuevoEvento.setEstado(crearEventoDTO.estadoEvento());
        nuevoEvento.setFecha(crearEventoDTO.fecha());
        nuevoEvento.setCiudad(crearEventoDTO.ciudad());

        // Crear localidades
        List<Localidad> localidades = crearLocalidades(crearEventoDTO.listaLocalidades());
        nuevoEvento.setLocalidades(localidades);

        // Guardar el nuevo evento en la base de datos
        Evento eventoGuardado = eventoRepository.save(nuevoEvento);

        // Retornar algún identificador del evento o mensaje de éxito
        return "Evento creado con éxito. ID: " + eventoGuardado.getId();
    }

    /**
     * Metodo para crear localidades
     * @param listaLocalidadesDTO
     * @return lista de localidades
     */
    private List<Localidad> crearLocalidades(List<LocalidadDTO> listaLocalidadesDTO) {
        // Inicializar la lista con la capacidad correcta
        List<Localidad> localidades = new ArrayList<>(listaLocalidadesDTO.size());

        // Iterar sobre cada LocalidadDTO y crear una nueva Localidad
        for (LocalidadDTO localidadDTO : listaLocalidadesDTO) {
            Localidad localidad = new Localidad(
                    localidadDTO.nombre(),
                    0,
                    localidadDTO.capacidadMaxima(),
                    localidadDTO.precio()
            );
            // Agregar la localidad a la lista
            localidades.add(localidad);
        }
        return localidades;
    }

    @Override
    public String editarEvento(EditarEventoDTO editarEventoDTO) throws EventoException {
        return "";
    }

    @Override
    public String eliminarEvento(String id) throws EventoException {
        return "";
    }

    @Override
    public InformacionEventoDTO obtenerInformacionEvento(String id) throws EventoException {
        return null;
    }

    @Override
    public List<ItemEventoDTO> listarEventos() {
        return List.of();
    }

    @Override
    public List<ItemEventoDTO> filtrarEventos(FiltroEventoDTO filtroEventoDTO) {
        return List.of();
    }
}
