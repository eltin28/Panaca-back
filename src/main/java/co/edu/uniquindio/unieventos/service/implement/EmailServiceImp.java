package co.edu.uniquindio.unieventos.service.implement;

import co.edu.uniquindio.unieventos.dto.email.EmailDTO;
import co.edu.uniquindio.unieventos.exceptions.EmailException;
import co.edu.uniquindio.unieventos.service.service.EmailService;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;

@Service
public class EmailServiceImp implements EmailService {


    @Override
    @Async
    public void enviarCorreo(EmailDTO emailDTO) throws EmailException {

        Email email = EmailBuilder.startingBlank()
                .from("infounieventos10@gmail.com")
                .to(emailDTO.destinatario())
                .withSubject(emailDTO.asunto())
                .withPlainText(emailDTO.cuerpo())
                .buildEmail();

        try (Mailer mailer = MailerBuilder
                .withSMTPServer("smtp.gmail.com", 587, "infounieventos10@gmail.com", "knxwnhhkbzpcwvib")
                .withTransportStrategy(TransportStrategy.SMTP_TLS)
                .withDebugLogging(true)
                .buildMailer()) {

            mailer.sendMail(email);
        } catch (Exception e) {
            e.printStackTrace();
            throw new EmailException("Error en el envio del correo");
        }
    }

}
