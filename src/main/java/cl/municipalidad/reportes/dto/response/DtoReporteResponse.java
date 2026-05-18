package cl.municipalidad.reportes.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoReporteResponse {
    private Long idReporte;
    private String tipoReporte;
    private LocalDate desde;
    private LocalDate hasta;
    private Double totalRecaudado;
    private Integer totalReservas;
    private String canchaEstrella; // La cancha más solicitada en ese periodo
    private LocalDateTime fechaGeneracion;
    private String operadorResponsable;
}