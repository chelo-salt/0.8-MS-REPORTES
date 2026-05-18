package cl.municipalidad.reportes.service;

import cl.municipalidad.reportes.client.AnaliticaClient;
import cl.municipalidad.reportes.dto.request.DtoGenerarReporteRequest;
import cl.municipalidad.reportes.dto.response.DtoReporteResponse;
import cl.municipalidad.reportes.model.ReporteModel;
import cl.municipalidad.reportes.repository.ReporteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor // 🚀 Inyecta el repositorio y el cliente de red por constructor automáticamente
public class ReporteService {

    private final ReporteRepository reporteRepository;
    private final AnaliticaClient analiticaClient;

    public DtoReporteResponse procesarYGuardarReporte(DtoGenerarReporteRequest request) {
        
        // 1. 🛡️ Validación de consistencia temporal
        if (request.getFechaInicio().isAfter(request.getFechaFin())) {
            throw new RuntimeException("Error de negocio: La fecha de inicio no puede ser posterior a la fecha de fin.");
        }

        // 2. 📡 Orquestación y consumo de microservicios externos mediante WebClient síncrono (.block())
        Double recaudacionTotal = analiticaClient.obtenerRecaudacionPorRango(request.getFechaInicio(), request.getFechaFin()).block();
        Integer cantidadReservas = analiticaClient.obtenerTotalReservasPorRango(request.getFechaInicio(), request.getFechaFin()).block();

        // 3. 🎯 Algoritmo de simulación analítica para determinar la cancha estrella del período
        String canchaEstrella = "Complejo Principal - Cancha de Fútbol 11"; 
        if (cantidadReservas != null && cantidadReservas > 0 && cantidadReservas % 2 == 0) {
            canchaEstrella = "Complejo Municipal - Cancha de Tenis Rápida";
        }

        // 4. 🗄️ Construcción y mapeo del modelo para persistencia local en db_reportes
        ReporteModel modelo = new ReporteModel();
        modelo.setTipoReporte(request.getTipoReporte().toUpperCase());
        modelo.setFechaInicioRango(request.getFechaInicio());
        modelo.setFechaFinRango(request.getFechaFin());
        modelo.setTotalRecaudado(recaudacionTotal != null ? recaudacionTotal : 0.0);
        modelo.setTotalReservas(cantidadReservas != null ? cantidadReservas : 0);
        modelo.setCanchaMasSolicitada(canchaEstrella);
        modelo.setFechaGeneracion(LocalDateTime.now());
        modelo.setGeneradoPor(request.getGeneradoPor());

        ReporteModel registroGuardado = reporteRepository.save(modelo);

        // 5. 📤 Mapeo y formateo final hacia el DTO de respuesta para el panel de administración
        DtoReporteResponse response = new DtoReporteResponse();
        response.setIdReporte(registroGuardado.getId());
        response.setTipoReporte(registroGuardado.getTipoReporte());
        response.setDesde(registroGuardado.getFechaInicioRango());
        response.setHasta(registroGuardado.getFechaFinRango());
        response.setTotalRecaudado(registroGuardado.getTotalRecaudado());
        response.setTotalReservas(registroGuardado.getTotalReservas());
        response.setCanchaEstrella(registroGuardado.getCanchaMasSolicitada());
        response.setFechaGeneracion(registroGuardado.getFechaGeneracion());
        response.setOperadorResponsable(registroGuardado.getGeneradoPor());

        return response;
    }
}