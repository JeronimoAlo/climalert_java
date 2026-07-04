package ar.edu.utn.frba.ddsi.climalert.services;

import ar.edu.utn.frba.ddsi.climalert.client.ProveedorClimatico;
import ar.edu.utn.frba.ddsi.climalert.dto.WeatherApiResponse;
import ar.edu.utn.frba.ddsi.climalert.exceptions.WeatherApiException;
import ar.edu.utn.frba.ddsi.climalert.models.entities.WeatherRecord;
import ar.edu.utn.frba.ddsi.climalert.models.repositories.WeatherRecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeatherIngestionServiceTest {

    private static final String UBICACION = "Buenos Aires,Argentina";

    @Mock
    private ProveedorClimatico proveedorClimatico;

    @Mock
    private WeatherRecordRepository weatherRecordRepository;

    @Test
    void guardaElRegistroMapeadoCuandoElProveedorRespondeOk() {
        WeatherApiResponse respuesta = new WeatherApiResponse(
                new WeatherApiResponse.Location("Buenos Aires", "CABA", "Argentina", "2026-07-04 10:00"),
                new WeatherApiResponse.Current("2026-07-04 10:00", 36.5, 65, new WeatherApiResponse.Condition("Sunny"))
        );
        when(proveedorClimatico.obtenerClimaActual(UBICACION)).thenReturn(respuesta);
        WeatherIngestionService service =
                new WeatherIngestionService(proveedorClimatico, weatherRecordRepository, UBICACION);

        service.obtenerYRegistrarClimaActual();

        ArgumentCaptor<WeatherRecord> captor = ArgumentCaptor.forClass(WeatherRecord.class);
        verify(weatherRecordRepository).save(captor.capture());
        WeatherRecord guardado = captor.getValue();

        assertThat(guardado.getUbicacion()).isEqualTo("Buenos Aires");
        assertThat(guardado.getTemperaturaC()).isEqualTo(36.5);
        assertThat(guardado.getHumedad()).isEqualTo(65);
        assertThat(guardado.getCondicionTexto()).isEqualTo("Sunny");
        assertThat(guardado.getObservadoEn())
                .isEqualTo(LocalDateTime.parse("2026-07-04 10:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        assertThat(guardado.isAlertaDisparada()).isFalse();
        assertThat(guardado.getObtenidoEn()).isNotNull();
    }

    @Test
    void noPropagaLaExcepcionNiGuardaNadaSiElProveedorFalla() {
        when(proveedorClimatico.obtenerClimaActual(UBICACION))
                .thenThrow(new WeatherApiException("fallo simulado", new RuntimeException("timeout")));
        WeatherIngestionService service =
                new WeatherIngestionService(proveedorClimatico, weatherRecordRepository, UBICACION);

        assertThatCode(service::obtenerYRegistrarClimaActual).doesNotThrowAnyException();

        verify(weatherRecordRepository, never()).save(any());
    }
}
