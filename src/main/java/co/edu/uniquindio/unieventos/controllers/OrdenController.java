package co.edu.uniquindio.unieventos.controllers;


import co.edu.uniquindio.unieventos.repository.OrdenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/Orden")
public class OrdenController {

    @Autowired
    private OrdenRepository ordenRepo;

}
