package ar.edu.utn.frba.ddsi.climalert.models.repositories;

import ar.edu.utn.frba.ddsi.climalert.models.entities.WeatherRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class WeatherRecordRepositoryTest {

    @Autowired
    private WeatherRecordRepository repository;

    @Test
    void devuelveElRegistroMasReciente() {
        repository.save(registroObtenidoEn(LocalDateTime.of(2026, 7, 4, 10, 0)));
        WeatherRecord masReciente = repository.save(registroObtenidoEn(LocalDateTime.of(2026, 7, 4, 10, 10)));
        repository.save(registroObtenidoEn(LocalDateTime.of(2026, 7, 4, 10, 5)));

        assertThat(repository.findTopByOrderByObtenidoEnDesc())
                .isPresent()
                .get()
                .extracting(WeatherRecord::getId)
                .isEqualTo(masReciente.getId());
    }

    @Test
    void devuelveVacioSiNoHayRegistros() {
        assertThat(repository.findTopByOrderByObtenidoEnDesc()).isEmpty();
    }

    private WeatherRecord registroObtenidoEn(LocalDateTime obtenidoEn) {
        WeatherRecord registro = new WeatherRecord();
        registro.setUbicacion("Buenos Aires");
        registro.setTemperaturaC(20.0);
        registro.setHumedad(50);
        registro.setCondicionTexto("Clear");
        registro.setObservadoEn(obtenidoEn);
        registro.setObtenidoEn(obtenidoEn);
        return registro;
    }
}
