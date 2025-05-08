package Panaca.service.implement;

import Panaca.dto.evento.*;
import Panaca.model.documents.Evento;
import Panaca.model.enums.EstadoEvento;
import Panaca.service.service.EventoService;
import Panaca.exceptions.EventoException;
import Panaca.repository.EventoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class EventoServiceImp implements EventoService {

    private final EventoRepository eventoRepo;
    private final MongoTemplate mongoTemplate;

    //Metodo para obtener un evento mediante el id
    private Optional<Evento> obtenerEventoPorId(String idEvento) throws EventoException {
        try {
            return eventoRepo.findById(idEvento);
        } catch (Exception e) {
            throw new EventoException("Error al obtener el evento con ID: " + idEvento);
        }
    }

    @Override
    public void crearEvento(CrearEventoDTO crearEventoDTO) throws EventoException {
        Evento nuevoEvento = new Evento();
        nuevoEvento.setNombre(crearEventoDTO.nombre());
        nuevoEvento.setDescripcion(crearEventoDTO.descripcion());
        nuevoEvento.setImagenPortada(crearEventoDTO.imagenPortada());
        nuevoEvento.setTipo(crearEventoDTO.tipo());
        nuevoEvento.setPrecio(crearEventoDTO.precio());
        nuevoEvento.setEstado(crearEventoDTO.estado());

        eventoRepo.save(nuevoEvento);
    }


    /**
     * Metodo para editar los atributos de un evento a partir de el DTO
     * param editarEventoDTO
     * return evento editado
     * throws EventoException
     */
    @Override
    public void editarEvento(String id, EditarEventoDTO editarEventoDTO) throws EventoException {
        Optional<Evento> optionalEvento = obtenerEventoPorId(id);

        if (optionalEvento.isEmpty()) {
            throw new EventoException("No existe este evento");
        }

        Evento eventoModificado = optionalEvento.get();
        eventoModificado.setImagenPortada(editarEventoDTO.imagenPortada());
        eventoModificado.setNombre(editarEventoDTO.nombre());
        eventoModificado.setDescripcion(editarEventoDTO.descripcion());
        eventoModificado.setTipo(editarEventoDTO.tipoEvento());
        eventoModificado.setEstado(editarEventoDTO.estadoEvento());
        eventoModificado.setPrecio(editarEventoDTO.precio());

        eventoRepo.save(eventoModificado);
    }

    /**
     * Metodo para eliminar un evento cambiando su estado a Eliminado
     * param id
     * return id del evento eliminado
     * throws EventoException
     */
    @Override
    public void eliminarEvento(String id) throws EventoException {
        Evento evento = obtenerEventoPorId(id)
                .orElseThrow(() -> new EventoException("No se encontró el evento con ID: " + id));

        evento.setEstado(EstadoEvento.ELIMINADO);
        eventoRepo.save(evento);
    }

    @Override
    public Evento obtenerInformacionEvento(String id) throws EventoException {

        Optional<Evento> optionalEvento = obtenerEventoPorId(id);

        if(optionalEvento.isEmpty()){
            throw new EventoException("No se encontro el evento con el id: " +id);
        }

        return optionalEvento.get();
    }

    @Override
    public List<ItemEventoDTO> listarEventos() {

        List<Evento> eventos = eventoRepo.findAll();

        List<ItemEventoDTO> itemEventoDTO = new ArrayList<>();

        for (Evento evento : eventos) {
            itemEventoDTO.add(new ItemEventoDTO(
                    evento.getId(),
                    evento.getImagenPortada(),
                    evento.getNombre(),
                    evento.getDescripcion(),
                    evento.getTipo(),
                    evento.getPrecio()
            ));
        }
        return itemEventoDTO;
    }

    @Override
    public Page<ItemEventoDTO> getEventosInactivos(Pageable pageable){
        Page<Evento> eventos = eventoRepo.getEventosByEstado(EstadoEvento.INACTIVO, pageable);

        // Transformar cada Evento en un ItemEventoDTO

        return eventos.map(evento -> new ItemEventoDTO(
                evento.getId(),
                evento.getImagenPortada(),
                evento.getNombre(),
                evento.getDescripcion(),
                evento.getTipo(),
                evento.getPrecio()
        ));
    }

    /**
     * Metodo para obtener solamente los eventos que se encuentran activo ahora mismo para el index de los usuarios
     * return
     */
    @Override
    public Page<ItemEventoDTO> getEventoActivos(Pageable pageable){
        Page<Evento> eventos = eventoRepo.getEventosByEstado(EstadoEvento.ACTIVO, pageable);

        // Transformar cada Evento en un ItemEventoDTO

        return eventos.map(evento -> new ItemEventoDTO(
                evento.getId(),
                evento.getImagenPortada(),
                evento.getNombre(),
                evento.getDescripcion(),
                evento.getTipo(),
                evento.getPrecio()
        ));
    }

    @Override
    public List<EventoFiltradoDTO> filtrarEventos(EventoFiltradoDTO filtro) {
        List<Criteria> criterios = new ArrayList<>();

        if (filtro.nombre() != null && !filtro.nombre().isBlank()) {
            criterios.add(Criteria.where("nombre").regex(filtro.nombre(), "i"));
        }

        if (filtro.tipoEvento() != null) {
            criterios.add(Criteria.where("tipo").is(filtro.tipoEvento()));
        }

        Query query = new Query();
        if (!criterios.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criterios.toArray(new Criteria[0])));
        }

        List<Evento> eventos = mongoTemplate.find(query, Evento.class);

        if (eventos.isEmpty()) {
            throw new EventoException("No se encontraron eventos para los filtros proporcionados.");
        }

        return eventos.stream()
                .map(this::convertirAEventoFiltradoDTO)
                .collect(Collectors.toList());
    }

    // Metodo privado para convertir un Evento en EventoFiltradoDTO
    private EventoFiltradoDTO convertirAEventoFiltradoDTO(Evento evento) {
        return new EventoFiltradoDTO(
                evento.getNombre(),
                evento.getTipo()
        );
    }
}