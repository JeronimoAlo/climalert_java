package ar.edu.utn.frba.ddsi.climalert.services.reglas;

import ar.edu.utn.frba.ddsi.climalert.models.entities.WeatherRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ReglaTemperaturaHumedadAlta implements ReglaDeAlerta {

    private final double umbralTemperaturaC;
    private final int umbralHumedad;

    public ReglaTemperaturaHumedadAlta(
            @Value("${climalert.alert.temperature-threshold-c}") double umbralTemperaturaC,
            @Value("${climalert.alert.humidity-threshold}") int umbralHumedad
    ) {
        this.umbralTemperaturaC = umbralTemperaturaC;
        this.umbralHumedad = umbralHumedad;
    }

    @Override
    public boolean seCumplePara(WeatherRecord registro) {
        return registro.getTemperaturaC() > umbralTemperaturaC
                && registro.getHumedad() > umbralHumedad;
    }
}
