package ar.edu.utn.frba.ddsi.climalert.services;

import ar.edu.utn.frba.ddsi.climalert.models.entities.WeatherRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
public class EmailService {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final JavaMailSender mailSender;
    private final String remitente;
    private final List<String> destinatarios;

    public EmailService(
            JavaMailSender mailSender,
            @Value("${climalert.alert.sender}") String remitente,
            @Value("${climalert.alert.recipients}") List<String> destinatarios
    ) {
        this.mailSender = mailSender;
        this.remitente = remitente;
        this.destinatarios = destinatarios;
    }

    public void enviarAlerta(WeatherRecord registro) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(remitente);
        mensaje.setTo(destinatarios.toArray(new String[0]));
        mensaje.setSubject("[Climalert] Alerta climatica en " + registro.getUbicacion());
        mensaje.setText(construirCuerpo(registro));
        mailSender.send(mensaje);
    }

    private String construirCuerpo(WeatherRecord registro) {
        return String.format(
                Locale.US,
                """
                Se detecto una condicion climatica peligrosa.

                Ubicacion: %s
                Temperatura: %.1f C
                Humedad: %d%%
                Condicion: %s
                Dato observado el (hora local de la ubicacion): %s
                Registrado por Climalert el (hora del servidor): %s
                """,
                registro.getUbicacion(),
                registro.getTemperaturaC(),
                registro.getHumedad(),
                registro.getCondicionTexto(),
                registro.getObservadoEn().format(FORMATO_FECHA),
                registro.getObtenidoEn().format(FORMATO_FECHA)
        );
    }
}
