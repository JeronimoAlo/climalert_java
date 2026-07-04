package ar.edu.utn.frba.ddsi.climalert.services;

import ar.edu.utn.frba.ddsi.climalert.events.AlertaClimaticaEvent;
import ar.edu.utn.frba.ddsi.climalert.models.entities.WeatherRecord;
import ar.edu.utn.frba.ddsi.climalert.models.repositories.WeatherRecordRepository;
import ar.edu.utn.frba.ddsi.climalert.services.reglas.ReglaDeAlerta;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

    private static final ReglaDeAlerta REGLA_QUE_SIEMPRE_CUMPLE = registro -> true;
    private static final ReglaDeAlerta REGLA_QUE_NUNCA_CUMPLE = registro -> false;

    @Mock
    private WeatherRecordRepository weatherRecordRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Test
    void noHaceNadaSiNoHayRegistrosGuardados() {
        when(weatherRecordRepository.findTopByOrderByObtenidoEnDesc()).thenReturn(Optional.empty());
        AlertService alertService =
                new AlertService(weatherRecordRepository, List.of(REGLA_QUE_SIEMPRE_CUMPLE), eventPublisher);

        alertService.evaluarUltimoRegistro();

        verify(weatherRecordRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void noReevaluaUnRegistroQueYaDisparoUnaAlerta() {
        WeatherRecord registro = new WeatherRecord();
        registro.setAlertaDisparada(true);
        when(weatherRecordRepository.findTopByOrderByObtenidoEnDesc()).thenReturn(Optional.of(registro));
        AlertService alertService =
                new AlertService(weatherRecordRepository, List.of(REGLA_QUE_SIEMPRE_CUMPLE), eventPublisher);

        alertService.evaluarUltimoRegistro();

        verify(weatherRecordRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void noDisparaAlertaSiNingunaReglaSeCumple() {
        WeatherRecord registro = new WeatherRecord();
        when(weatherRecordRepository.findTopByOrderByObtenidoEnDesc()).thenReturn(Optional.of(registro));
        AlertService alertService =
                new AlertService(weatherRecordRepository, List.of(REGLA_QUE_NUNCA_CUMPLE), eventPublisher);

        alertService.evaluarUltimoRegistro();

        assertThat(registro.isAlertaDisparada()).isFalse();
        verify(weatherRecordRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void disparaLaAlertaSiAlMenosUnaReglaSeCumple() {
        WeatherRecord registro = new WeatherRecord();
        registro.setId(1L);
        when(weatherRecordRepository.findTopByOrderByObtenidoEnDesc()).thenReturn(Optional.of(registro));
        AlertService alertService = new AlertService(
                weatherRecordRepository,
                List.of(REGLA_QUE_NUNCA_CUMPLE, REGLA_QUE_SIEMPRE_CUMPLE),
                eventPublisher);

        alertService.evaluarUltimoRegistro();

        assertThat(registro.isAlertaDisparada()).isTrue();
        verify(weatherRecordRepository, times(1)).save(registro);

        ArgumentCaptor<AlertaClimaticaEvent> eventCaptor = ArgumentCaptor.forClass(AlertaClimaticaEvent.class);
        verify(eventPublisher, times(1)).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getRegistro()).isSameAs(registro);
    }
}
