package cl.municipalidad.reportes.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reportes_generados")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReporteModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tipoReporte; // Ej: "FINANCIERO", "OCUPACION", "RENDIMIENTO"

    @Column(nullable = false)
    private LocalDate fechaInicioRango; // Desde cuándo filtra el reporte

    @Column(nullable = false)
    private LocalDate fechaFinRango; // Hasta cuándo filtra el reporte

    @Column(nullable = false)
    private Double totalRecaudado; // Monto calculado acumulado

    @Column(nullable = false)
    private Integer totalReservas; // Cantidad de reservas procesadas en ese rango

    @Column(nullable = false)
    private String canchaMasSolicitada; // Nombre de la cancha estrella en ese periodo

    @Column(nullable = false)
    private LocalDateTime fechaGeneracion; // Timestamp de auditoría

    @Column(nullable = false)
    private String generadoPor; // Nombre o ID del administrador que lo solicitó
}