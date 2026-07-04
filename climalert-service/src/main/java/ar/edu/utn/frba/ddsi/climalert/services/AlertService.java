package ar.edu.utn.frba.ddsi.climalert.services;

import ar.edu.utn.frba.ddsi.climalert.events.AlertaClimaticaEvent;
import ar.edu.utn.frba.ddsi.climalert.models.entities.WeatherRecord;
import ar.edu.utn.frba.ddsi.climalert.models.repositories.WeatherRecordRepository;
import ar.edu.utn.frba.ddsi.climalert.services.reglas.ReglaDeAlerta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlertService {

    private static final Logger log = LoggerFactory.getLogger(AlertService.class);

    private final WeatherRecordRepository weatherRecordRepository;
    private final List<ReglaDeAlerta> reglasDeAlerta;
    private final ApplicationEventPublisher eventPublisher;

    public AlertService(
            WeatherRecordRepository weatherRecordRepository,
            List<ReglaDeAlerta> reglasDeAlerta,
            ApplicationEventPublisher eventPublisher
    ) {
        this.weatherRecordRepository = weatherRecordRepository;
        this.reglasDeAlerta = reglasDeAlerta;
        this.eventPublisher = eventPublisher;
    }

    public void evaluarUltimoRegistro() {
        Optional<WeatherRecord> ultimoRegistro = weatherRecordRepository.findTopByOrderByObtenidoEnDesc();
        if (ultimoRegistro.isEmpty()) {
            log.debug("Aun no hay registros climaticos para evaluar");
            return; // Aún no se ejecuto el CRON de obtencion de informacion climatica
        }

        WeatherRecord registro = ultimoRegistro.get();
        if (registro.isAlertaDisparada()) {
            log.debug("El ultimo registro ({}) ya disparo una alerta, no se reevalua", registro.getId());
            return; // Si ya se disparo la alerta no volvemos a hacerlo.
        }

        boolean cumpleAlgunaRegla = reglasDeAlerta.stream().anyMatch(regla -> regla.seCumplePara(registro));
        if (!cumpleAlgunaRegla) {
            log.info("Clima dentro de parametros normales: {}°C, {}% humedad", registro.getTemperaturaC(), registro.getHumedad());
            return;
        }

        // Si alguna regla se cumple, disparamos la alerta.
        registro.setAlertaDisparada(true);
        weatherRecordRepository.save(registro); // Marcamos el registro como "alertado"

        log.info("Condicion de alerta detectada para el registro {}, publicando evento", registro.getId());
        // Publica el evento, sin importar quien lo va a procesar luego (springboot hace el resto)
        eventPublisher.publishEvent(new AlertaClimaticaEvent(this, registro));
    }
}
