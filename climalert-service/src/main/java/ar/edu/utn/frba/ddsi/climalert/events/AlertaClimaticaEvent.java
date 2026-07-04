package ar.edu.utn.frba.ddsi.climalert.events;

import ar.edu.utn.frba.ddsi.climalert.models.entities.WeatherRecord;
import org.springframework.context.ApplicationEvent;

public class AlertaClimaticaEvent extends ApplicationEvent {

    private final WeatherRecord registro;

    public AlertaClimaticaEvent(Object source, WeatherRecord registro) {
        super(source);
        this.registro = registro;
    }

    public WeatherRecord getRegistro() {
        return registro;
    }
}
