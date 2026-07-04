package ar.edu.utn.frba.ddsi.climalert.schedulers;

import ar.edu.utn.frba.ddsi.climalert.services.WeatherIngestionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class WeatherIngestionScheduler {

    private final WeatherIngestionService weatherIngestionService;

    public WeatherIngestionScheduler(WeatherIngestionService weatherIngestionService) {
        this.weatherIngestionService = weatherIngestionService;
    }

    @Scheduled(fixedRateString = "PT5M") // Cada 5 minutos.
    public void ejecutar() {
        weatherIngestionService.obtenerYRegistrarClimaActual();
    }
}
