package ar.edu.utn.frba.ddsi.climalert.config;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Resuelve el directorio de climalert-service sin importar desde donde se haya
 * lanzado el proceso (raiz del repo, el propio modulo, o un contenedor Docker).
 */
public final class AppHome {

    private static final String MODULE_DIR_NAME = "climalert-service";

    private AppHome() {
    }

    public static Path directory() {
        Path currentDir = Path.of(System.getProperty("user.dir"));
        Path moduleDir = currentDir.resolve(MODULE_DIR_NAME);
        return Files.isDirectory(moduleDir) ? moduleDir : currentDir;
    }
}
