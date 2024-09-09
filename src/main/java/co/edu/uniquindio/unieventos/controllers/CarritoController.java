package co.edu.uniquindio.unieventos.controllers;

import co.edu.uniquindio.unieventos.repository.CarritoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/Carrito")
public class CarritoController {

    @Autowired
    private CarritoRepository carritoRepo;

}
