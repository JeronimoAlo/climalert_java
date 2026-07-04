package ar.edu.utn.frba.ddsi.climalert.models.repositories;

import ar.edu.utn.frba.ddsi.climalert.models.entities.WeatherRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// JPA Repository se encarga de generar los métodos correspondientes (se encarga de almacenar en la DB definida)
public interface WeatherRecordRepository extends JpaRepository<WeatherRecord, Long> {

    Optional<WeatherRecord> findTopByOrderByObtenidoEnDesc(); // JPA se encarga de generar la query dinamica
}
