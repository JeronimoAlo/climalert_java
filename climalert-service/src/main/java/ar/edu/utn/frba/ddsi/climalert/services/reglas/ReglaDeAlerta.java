package ar.edu.utn.frba.ddsi.climalert.services.reglas;

import ar.edu.utn.frba.ddsi.climalert.models.entities.WeatherRecord;

// El dia de mañana cuando se agregue alguna regla mas, implementara la interfaz
public interface ReglaDeAlerta {

    // Metodo encargado de validar si la regla aplica para el registro climatico en cuestion
    boolean seCumplePara(WeatherRecord registro);
}
