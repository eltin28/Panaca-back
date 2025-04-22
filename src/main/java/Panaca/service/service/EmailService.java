package Panaca.service.service;

import Panaca.dto.email.EmailDTO;
import Panaca.exceptions.EmailException;
import org.springframework.stereotype.Service;

@Service
public interface EmailService {

    void enviarCorreo(EmailDTO emailDTO) throws EmailException;

}
