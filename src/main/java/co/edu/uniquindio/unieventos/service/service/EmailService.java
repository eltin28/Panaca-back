package co.edu.uniquindio.unieventos.service.service;

import co.edu.uniquindio.unieventos.dto.email.EmailDTO;
import co.edu.uniquindio.unieventos.exceptions.EmailException;
import org.springframework.stereotype.Service;

@Service
public interface EmailService {

    void enviarCorreo(EmailDTO emailDTO) throws EmailException;

}
