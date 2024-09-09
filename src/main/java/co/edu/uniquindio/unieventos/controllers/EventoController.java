package co.edu.uniquindio.unieventos.controllers;


import co.edu.uniquindio.unieventos.repository.EventoRepository;
import co.edu.uniquindio.unieventos.service.EventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/Evento")
public class EventoController {

    @Autowired
    private EventoRepository eventoRepo;

}
