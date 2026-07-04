package ar.edu.utn.frba.ddsi.climalert.models.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WeatherRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ubicacion;
    private Double temperaturaC;
    private Integer humedad;
    private String condicionTexto;
    private LocalDateTime observadoEn;
    private LocalDateTime obtenidoEn;
    private boolean alertaDisparada;
}
