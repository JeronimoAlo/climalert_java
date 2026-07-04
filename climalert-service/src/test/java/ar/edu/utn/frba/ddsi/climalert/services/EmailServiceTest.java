package ar.edu.utn.frba.ddsi.climalert.services;

import ar.edu.utn.frba.ddsi.climalert.models.entities.WeatherRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    private static final List<String> DESTINATARIOS =
            List.of("admin@clima.com", "emergencias@clima.com", "meteorologia@clima.com");

    @Mock
    private JavaMailSender mailSender;

    @Test
    void enviaUnMailConElDetalleCompletoATodosLosDestinatarios() {
        EmailService emailService = new EmailService(mailSender, "no-reply@climalert.com", DESTINATARIOS);
        WeatherRecord registro = new WeatherRecord();
        registro.setUbicacion("Buenos Aires");
        registro.setTemperaturaC(36.5);
        registro.setHumedad(65);
        registro.setCondicionTexto("Sunny");
        registro.setObservadoEn(LocalDateTime.of(2026, 7, 4, 10, 0));
        registro.setObtenidoEn(LocalDateTime.of(2026, 7, 4, 10, 1));

        emailService.enviarAlerta(registro);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        SimpleMailMessage mensaje = captor.getValue();

        assertThat(mensaje.getFrom()).isEqualTo("no-reply@climalert.com");
        assertThat(mensaje.getTo()).containsExactly(
                "admin@clima.com", "emergencias@clima.com", "meteorologia@clima.com");
        assertThat(mensaje.getSubject()).contains("Buenos Aires");
        assertThat(mensaje.getText())
                .contains("Buenos Aires")
                .contains("36.5")
                .contains("65%")
                .contains("Sunny")
                .contains("04/07/2026 10:00")
                .contains("04/07/2026 10:01");
    }
}
