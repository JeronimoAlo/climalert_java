package ar.edu.utn.frba.ddsi.climalert.services.reglas;

import ar.edu.utn.frba.ddsi.climalert.models.entities.WeatherRecord;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReglaTemperaturaHumedadAltaTest {

    private final ReglaTemperaturaHumedadAlta regla = new ReglaTemperaturaHumedadAlta(35, 60);

    @Test
    void seCumpleCuandoTemperaturaYHumedadSuperanElUmbral() {
        WeatherRecord registro = registroCon(36.0, 61);

        assertThat(regla.seCumplePara(registro)).isTrue();
    }

    @Test
    void noSeCumpleSiSoloLaTemperaturaSuperaElUmbral() {
        WeatherRecord registro = registroCon(36.0, 55);

        assertThat(regla.seCumplePara(registro)).isFalse();
    }

    @Test
    void noSeCumpleSiSoloLaHumedadSuperaElUmbral() {
        WeatherRecord registro = registroCon(30.0, 70);

        assertThat(regla.seCumplePara(registro)).isFalse();
    }

    @Test
    void noSeCumpleSiNingunaCondicionSeSupera() {
        WeatherRecord registro = registroCon(20.0, 40);

        assertThat(regla.seCumplePara(registro)).isFalse();
    }

    @Test
    void noSeCumpleEnElValorExactoDelUmbralDeTemperatura() {
        WeatherRecord registro = registroCon(35.0, 70);

        assertThat(regla.seCumplePara(registro)).isFalse();
    }

    @Test
    void noSeCumpleEnElValorExactoDelUmbralDeHumedad() {
        WeatherRecord registro = registroCon(36.0, 60);

        assertThat(regla.seCumplePara(registro)).isFalse();
    }

    @Test
    void respetaUmbralesConfiguradosDistintosAlDefault() {
        ReglaTemperaturaHumedadAlta reglaMasEstricta = new ReglaTemperaturaHumedadAlta(40, 80);
        WeatherRecord registro = registroCon(36.0, 65);

        assertThat(reglaMasEstricta.seCumplePara(registro)).isFalse();
    }

    private WeatherRecord registroCon(double temperaturaC, int humedad) {
        WeatherRecord registro = new WeatherRecord();
        registro.setTemperaturaC(temperaturaC);
        registro.setHumedad(humedad);
        return registro;
    }
}
