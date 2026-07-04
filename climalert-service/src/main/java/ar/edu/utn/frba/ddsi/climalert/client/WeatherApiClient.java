package ar.edu.utn.frba.ddsi.climalert.client;

import ar.edu.utn.frba.ddsi.climalert.dto.WeatherApiResponse;
import ar.edu.utn.frba.ddsi.climalert.exceptions.WeatherApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class WeatherApiClient implements ProveedorClimatico {

    private final RestClient restClient;
    private final String apiKey;

    public WeatherApiClient(
            @Value("${climalert.weather-api.base-url}") String baseUrl,
            @Value("${climalert.weather-api.key}") String apiKey
    ) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
        this.apiKey = apiKey;
    }

    @Override
    public WeatherApiResponse obtenerClimaActual(String ubicacion) {
        try {
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/current.json")
                            .queryParam("key", apiKey)
                            .queryParam("q", ubicacion)
                            .build())
                    .retrieve()
                    .body(WeatherApiResponse.class);
        } catch (RestClientException e) {
            throw new WeatherApiException("No se pudo obtener el clima actual para " + ubicacion, e);
        }
    }
}
