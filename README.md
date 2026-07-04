# Climalert

Servicio autónomo (sin interfaz gráfica) que monitorea el clima de una ubicación fija
consultando periódicamente [WeatherAPI](https://www.weatherapi.com/), guarda un histórico
local y envía alertas por correo electrónico cuando detecta condiciones peligrosas
(temperatura y humedad por encima de los umbrales configurados).

Construido con **Java 21** y **Spring Boot**, organizado como proyecto Maven multi-módulo:

- `common-lib`: utilidades compartidas.
- `climalert-service`: el servicio en sí (ingesta de clima, reglas de alerta, envío de emails).

## Requisitos

- JDK 21
- Maven 3.9+ (o usar el wrapper `./mvnw` si está disponible)
- Una API key gratuita de [WeatherAPI](https://www.weatherapi.com/)
- Credenciales SMTP para el envío de correos (el proyecto está configurado por defecto
  para usar [Brevo](https://app.brevo.com/settings/keys/smtp), pero cualquier SMTP sirve
  cambiando `spring.mail.*` en `application.properties`)

No se necesita ninguna de estas credenciales para compilar el proyecto ni para correr los tests.

## Configuración

1. Copiar el archivo de variables de entorno de ejemplo:

   ```bash
   cp climalert-service/.env.template climalert-service/.env
   ```

2. Completar `climalert-service/.env` con tus credenciales:

   ```dotenv
   WEATHERAPI_KEY=tu_api_key_de_weatherapi
   BREVO_SMTP_USERNAME=tu_usuario_smtp
   BREVO_SMTP_PASSWORD=tu_password_smtp
   BREVO_SMTP_SENDER=no-reply@climalert.com
   ```

   El archivo `.env` es local y nunca se sube al repositorio (ver `.gitignore`). Se carga
   automáticamente al iniciar la aplicación (`DotenvLoader`); si preferís no usar `.env`,
   podés exportar las mismas variables como variables de entorno del sistema operativo,
   que siempre tienen prioridad.

3. (Opcional) Ajustar en `climalert-service/src/main/resources/application.properties`:
   - `climalert.location`: ubicación a monitorear (por defecto `Dubai,United Arab Emirates`).
   - `climalert.alert.temperature-threshold-c` / `climalert.alert.humidity-threshold`: umbrales de alerta.
   - `climalert.alert.recipients`: destinatarios de los correos de alerta.

## Compilar

Desde la raíz del repositorio (compila `common-lib` y `climalert-service`):

```bash
mvn clean install
```

## Correr los tests

```bash
mvn test
```

Los tests no requieren API key ni credenciales reales: las integraciones externas
(WeatherAPI, envío de correo) están mockeadas.

## Levantar el servicio localmente

```bash
mvn -pl climalert-service -am spring-boot:run
```

O, tras compilar el jar:

```bash
mvn clean package -pl climalert-service -am -DskipTests
java -jar climalert-service/target/climalert-service-*.jar
```

El servicio arranca en `http://localhost:8080`, comienza a:

- Consultar WeatherAPI cada 5 minutos y guardar el resultado en una base H2 local (`climalert-service/data/`).
- Evaluar la última lectura cada 1 minuto y, si detecta condiciones de alerta, enviar un email a los destinatarios configurados.

## Correr con Docker

```bash
docker build -t climalert-service .
docker run --env-file climalert-service/.env -p 8080:8080 climalert-service
```

## Integración continua

Cada push o pull request contra `main` ejecuta automáticamente `mvn test` vía GitHub
Actions (`.github/workflows/tests.yml`), para detectar regresiones antes de mergear.
