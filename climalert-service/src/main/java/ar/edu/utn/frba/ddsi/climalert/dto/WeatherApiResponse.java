package ar.edu.utn.frba.ddsi.climalert.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WeatherApiResponse(Location location, Current current) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Location(String name, String region, String country, String localtime) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Current(
            @JsonProperty("last_updated") String lastUpdated,
            @JsonProperty("temp_c") Double tempC,
            Integer humidity,
            Condition condition
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Condition(String text) {
    }
}
