package ar.edu.utn.frba.ddsi.climalert.listeners;

import ar.edu.utn.frba.ddsi.climalert.events.AlertaClimaticaEvent;
import ar.edu.utn.frba.ddsi.climalert.models.entities.WeatherRecord;
import ar.edu.utn.frba.ddsi.climalert.services.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailAlertaListenerTest {

    @Mock
    private EmailService emailService;

    @Test
    void delegaEnEmailServiceConElRegistroDelEvento() {
        EmailAlertaListener listener = new EmailAlertaListener(emailService);
        WeatherRecord registro = new WeatherRecord();
        AlertaClimaticaEvent evento = new AlertaClimaticaEvent(this, registro);

        listener.onAlertaClimatica(evento);

        verify(emailService).enviarAlerta(registro);
    }
}
