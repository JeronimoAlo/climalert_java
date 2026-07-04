package ar.edu.utn.frba.ddsi.climalert.schedulers;

import ar.edu.utn.frba.ddsi.climalert.services.WeatherIngestionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WeatherIngestionSchedulerTest {

    @Mock
    private WeatherIngestionService weatherIngestionService;

    @Test
    void delegaEnWeatherIngestionService() {
        WeatherIngestionScheduler scheduler = new WeatherIngestionScheduler(weatherIngestionService);

        scheduler.ejecutar();

        verify(weatherIngestionService).obtenerYRegistrarClimaActual();
    }
}
