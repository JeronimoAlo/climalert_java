package ar.edu.utn.frba.ddsi.climalert;

import ar.edu.utn.frba.ddsi.climalert.config.AppHome;
import ar.edu.utn.frba.ddsi.climalert.config.DotenvLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ClimalertServiceApplication {
    public static void main(String[] args) {
        DotenvLoader.loadIfPresent(); // Cargamos el .env
        configurarDirectorioDeDatos();
        SpringApplication.run(ClimalertServiceApplication.class, args);
    }

    // Fija donde vive la base H2 sin importar desde donde se lance el proceso
    private static void configurarDirectorioDeDatos() {
        if (System.getProperty("CLIMALERT_DATA_DIR") == null) {
            String dataDir = AppHome.directory().resolve("data").toString().replace('\\', '/');
            System.setProperty("CLIMALERT_DATA_DIR", dataDir);
        }
    }
}
