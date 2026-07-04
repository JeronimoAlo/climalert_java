package ar.edu.utn.frba.ddsi.climalert.client;

import ar.edu.utn.frba.ddsi.climalert.dto.WeatherApiResponse;
import ar.edu.utn.frba.ddsi.climalert.exceptions.WeatherApiException;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WeatherApiClientTest {

    private static final String RESPUESTA_OK = """
            {
              "location": {
                "name": "Buenos Aires",
                "region": "Buenos Aires",
                "country": "Argentina",
                "localtime": "2026-07-04 10:00"
              },
              "current": {
                "last_updated": "2026-07-04 10:00",
                "temp_c": 36.5,
                "humidity": 65,
                "condition": {
                  "text": "Sunny"
                }
              }
            }
            """;

    private HttpServer server;

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void mapeaLaRespuestaDeWeatherApiCorrectamente() throws IOException {
        server = iniciarServidor(200, RESPUESTA_OK, null);
        WeatherApiClient client = new WeatherApiClient(baseUrlDe(server), "test-key");

        WeatherApiResponse respuesta = client.obtenerClimaActual("Buenos Aires,Argentina");

        assertThat(respuesta.location().name()).isEqualTo("Buenos Aires");
        assertThat(respuesta.current().tempC()).isEqualTo(36.5);
        assertThat(respuesta.current().humidity()).isEqualTo(65);
        assertThat(respuesta.current().condition().text()).isEqualTo("Sunny");
        assertThat(respuesta.current().lastUpdated()).isEqualTo("2026-07-04 10:00");
    }

    @Test
    void enviaLaApiKeyYLaUbicacionComoQueryParams() throws IOException {
        AtomicReference<String> queryCapturada = new AtomicReference<>();
        server = iniciarServidor(200, RESPUESTA_OK, queryCapturada);
        WeatherApiClient client = new WeatherApiClient(baseUrlDe(server), "test-key");

        client.obtenerClimaActual("Buenos Aires,Argentina");

        Map<String, String> query = parsearQuery(queryCapturada.get());
        assertThat(query).containsEntry("key", "test-key");
        assertThat(query).containsEntry("q", "Buenos Aires,Argentina");
    }

    @Test
    void lanzaWeatherApiExceptionSiElProveedorRespondeError() throws IOException {
        server = iniciarServidor(500, "<html>error</html>", null);
        WeatherApiClient client = new WeatherApiClient(baseUrlDe(server), "test-key");

        assertThatThrownBy(() -> client.obtenerClimaActual("Buenos Aires,Argentina"))
                .isInstanceOf(WeatherApiException.class)
                .hasCauseInstanceOf(RestClientException.class);
    }

    @Test
    void lanzaWeatherApiExceptionSiElServidorEsInalcanzable() throws IOException {
        server = iniciarServidor(200, RESPUESTA_OK, null);
        String baseUrl = baseUrlDe(server);
        server.stop(0);
        server = null;
        WeatherApiClient client = new WeatherApiClient(baseUrl, "test-key");

        assertThatThrownBy(() -> client.obtenerClimaActual("Buenos Aires,Argentina"))
                .isInstanceOf(WeatherApiException.class);
    }

    private HttpServer iniciarServidor(int status, String body, AtomicReference<String> queryCapturada) throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        httpServer.createContext("/current.json", exchange -> {
            if (queryCapturada != null) {
                queryCapturada.set(exchange.getRequestURI().getRawQuery());
            }
            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(status, bytes.length);
            try (var os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        });
        httpServer.start();
        return httpServer;
    }

    private String baseUrlDe(HttpServer httpServer) {
        return "http://localhost:" + httpServer.getAddress().getPort();
    }

    private Map<String, String> parsearQuery(String rawQuery) {
        Map<String, String> valores = new HashMap<>();
        for (String par : rawQuery.split("&")) {
            String[] partes = par.split("=", 2);
            String clave = URLDecoder.decode(partes[0], StandardCharsets.UTF_8);
            String valor = URLDecoder.decode(partes[1], StandardCharsets.UTF_8);
            valores.put(clave, valor);
        }
        return valores;
    }
}
