package ar.edu.utn.frba.ddsi.climalert.client;

import ar.edu.utn.frba.ddsi.climalert.dto.WeatherApiResponse;

// Interfaz para el día de mañana agregar más proveedores climaticos
public interface ProveedorClimatico {

    WeatherApiResponse obtenerClimaActual(String ubicacion);
}
