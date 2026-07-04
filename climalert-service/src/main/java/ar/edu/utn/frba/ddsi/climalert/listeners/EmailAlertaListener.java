package ar.edu.utn.frba.ddsi.climalert.listeners;

import ar.edu.utn.frba.ddsi.climalert.events.AlertaClimaticaEvent;
import ar.edu.utn.frba.ddsi.climalert.services.EmailService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class EmailAlertaListener {

    private final EmailService emailService;

    public EmailAlertaListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @EventListener
    // Springboot llamara a este metodo cuando se publique un evento en el servicio.
    public void onAlertaClimatica(AlertaClimaticaEvent evento) {
        emailService.enviarAlerta(evento.getRegistro());
    }
}
