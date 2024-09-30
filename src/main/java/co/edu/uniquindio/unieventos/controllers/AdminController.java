package co.edu.uniquindio.unieventos.controllers;

import co.edu.uniquindio.unieventos.dto.evento.CrearEventoDTO;
import co.edu.uniquindio.unieventos.repository.EventoRepository;
import co.edu.uniquindio.unieventos.service.service.EventoService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class AdminController {

    @Autowired
    EventoRepository eventoRepository;
    @Autowired
    EventoService eventoService;

    /**
     * Metodo que permite crear un evento a el administrador
     * @param eventoDTO
     * @return mensajes de error o confirmacion dependiendo de si la consulta se realizo correctamente
     */
    @PostMapping("/crear-evento")
    public ResponseEntity<?> crearEvento(@Valid @RequestBody CrearEventoDTO eventoDTO){
        try{
            String nuevoEvento = eventoService.crearEvento(eventoDTO);
            return new ResponseEntity<String>("Evento creado exitosamente", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<String>("Error creando el evento", HttpStatus.CONFLICT);
        }
    }


}
