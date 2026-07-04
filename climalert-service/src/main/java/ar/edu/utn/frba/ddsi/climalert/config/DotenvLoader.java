package ar.edu.utn.frba.ddsi.climalert.config;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Carga variables desde un archivo ".env" (si existe) como system properties, sin depender
 * de ninguna libreria externa. Una variable de entorno real del sistema operativo siempre
 * tiene prioridad por sobre el ".env" — este ultimo es solo para desarrollo local.
 */
public final class DotenvLoader {

    private static final String ENV_FILE_NAME = ".env";

    private DotenvLoader() {
    }

    public static void loadIfPresent() {
        Path envFile = AppHome.directory().resolve(ENV_FILE_NAME);
        if (Files.isRegularFile(envFile)) {
            load(envFile);
        }
    }

    private static void load(Path envFile) {
        try {
            for (String line : Files.readAllLines(envFile, StandardCharsets.UTF_8)) {
                aplicarLinea(line);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer el archivo " + envFile, e);
        }
    }

    private static void aplicarLinea(String line) {
        String trimmed = line.strip();
        if (trimmed.isEmpty() || trimmed.startsWith("#")) {
            return;
        }

        int separatorIndex = trimmed.indexOf('=');
        if (separatorIndex < 0) {
            return;
        }

        String key = trimmed.substring(0, separatorIndex).strip();
        String value = quitarComillas(trimmed.substring(separatorIndex + 1).strip());

        if (key.isEmpty() || value.isEmpty()) {
            return;
        }

        if (System.getenv(key) == null && System.getProperty(key) == null) {
            System.setProperty(key, value);
        }
    }

    private static String quitarComillas(String value) {
        if (value.length() >= 2) {
            char first = value.charAt(0);
            char last = value.charAt(value.length() - 1);
            if ((first == '"' && last == '"') || (first == '\'' && last == '\'')) {
                return value.substring(1, value.length() - 1);
            }
        }
        return value;
    }
}
