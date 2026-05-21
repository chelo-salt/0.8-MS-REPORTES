package cl.municipalidad.reportes.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoGenerarReporteRequest {

    @NotBlank(message = "El tipo de reporte (FINANCIERO, OCUPACION, etc.) es obligatorio.")
    private String tipoReporte;

    @NotNull(message = "La fecha de inicio para el rango de analítica es obligatoria.")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin para el rango de analítica es obligatoria.")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaFin;

    @NotBlank(message = "Debe especificar el nombre o ID del administrador que genera el reporte.")
    private String generadoPor;
}