package co.edu.uniquindio.unieventos.controllers;


import co.edu.uniquindio.unieventos.repository.CuponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/Cupon")
public class CuponController {

    @Autowired
    private CuponRepository cuponRepo;

}
