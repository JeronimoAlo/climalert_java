package ar.edu.utn.frba.ddsi.climalert.services;

import ar.edu.utn.frba.ddsi.climalert.client.ProveedorClimatico;
import ar.edu.utn.frba.ddsi.climalert.dto.WeatherApiResponse;
import ar.edu.utn.frba.ddsi.climalert.exceptions.WeatherApiException;
import ar.edu.utn.frba.ddsi.climalert.models.entities.WeatherRecord;
import ar.edu.utn.frba.ddsi.climalert.models.repositories.WeatherRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class WeatherIngestionService {

    private static final Logger log = LoggerFactory.getLogger(WeatherIngestionService.class);
    private static final DateTimeFormatter WEATHER_API_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final ProveedorClimatico proveedorClimatico;
    private final WeatherRecordRepository weatherRecordRepository;
    private final String ubicacion;

    public WeatherIngestionService(
            ProveedorClimatico proveedorClimatico,
            WeatherRecordRepository weatherRecordRepository,
            @Value("${climalert.location}") String ubicacion // Inyectamos como dependencia la ubicacion a consultar
    ) {
        this.proveedorClimatico = proveedorClimatico;
        this.weatherRecordRepository = weatherRecordRepository;
        this.ubicacion = ubicacion;
    }

    public void obtenerYRegistrarClimaActual() {
        try {
            WeatherApiResponse respuesta = proveedorClimatico.obtenerClimaActual(ubicacion);
            WeatherRecord registro = aWeatherRecord(respuesta);
            weatherRecordRepository.save(registro); // El repo se encarga de guardar en la BD
            log.info("Clima registrado para {}: {}°C, {}% humedad, {}",
                    registro.getUbicacion(), registro.getTemperaturaC(), registro.getHumedad(), registro.getCondicionTexto());
        } catch (WeatherApiException e) {
            log.error("Fallo al obtener el clima actual, se reintentara en el proximo ciclo", e);
        }
    }

    private WeatherRecord aWeatherRecord(WeatherApiResponse respuesta) {
        WeatherApiResponse.Current current = respuesta.current();

        WeatherRecord record = new WeatherRecord();
        record.setUbicacion(respuesta.location().name());
        record.setTemperaturaC(current.tempC());
        record.setHumedad(current.humidity());
        record.setCondicionTexto(current.condition().text());
        record.setObservadoEn(LocalDateTime.parse(current.lastUpdated(), WEATHER_API_DATE_FORMAT));
        record.setObtenidoEn(LocalDateTime.now());
        record.setAlertaDisparada(false);
        return record;
    }
}
