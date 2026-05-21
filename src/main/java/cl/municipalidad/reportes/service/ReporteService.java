package cl.municipalidad.reportes.service;

import cl.municipalidad.reportes.client.AnaliticaClient;
import cl.municipalidad.reportes.dto.request.DtoGenerarReporteRequest;
import cl.municipalidad.reportes.dto.response.DtoReporteResponse;
import cl.municipalidad.reportes.model.ReporteModel;
import cl.municipalidad.reportes.repository.ReporteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final ReporteRepository reporteRepository;
    private final AnaliticaClient analiticaClient;

    /**
     * Procesa la analítica en paralelo desde otros servicios, guarda el registro histórico
     * y retorna el DTO estandarizado.
     */
    @Transactional // 🗄️ Asegura la integridad transaccional de la persistencia
    public DtoReporteResponse procesarYGuardarReporte(DtoGenerarReporteRequest request, String token) {
        
        // 1. 🛡️ Validación de consistencia temporal
        if (request.getFechaInicio().isAfter(request.getFechaFin())) {
            throw new IllegalArgumentException("Error de negocio: La fecha de inicio no puede ser posterior a la fecha de fin.");
        }

        // 2. ⚡ Orquestación Reactiva en Paralelo (Mono.zip)
        // Las tres peticiones HTTP viajan al mismo tiempo. El tiempo total será igual al del servicio más lento.
        var consultasCombinadas = Mono.zip(
                analiticaClient.obtenerRecaudacionPorRango(request.getFechaInicio(), request.getFechaFin(), token),
                analiticaClient.obtenerTotalReservasPorRango(request.getFechaInicio(), request.getFechaFin(), token),
                analiticaClient.obtenerCanchaEstrellaPorRango(request.getFechaInicio(), request.getFechaFin(), token)
        ).block(); // 🛑 Un único punto de bloqueo para consolidar los resultados síncronamente

        // Extracción segura de los datos combinados
        Double recaudacionTotal = consultasCombinadas != null ? consultasCombinadas.getT1() : 0.0;
        Integer cantidadReservas = consultasCombinadas != null ? consultasCombinadas.getT2() : 0;
        String canchaEstrella = consultasCombinadas != null ? consultasCombinadas.getT3() : "Sin datos";

        // 3. 🗄️ Construcción del modelo (Nombres estandarizados)
        ReporteModel modelo = new ReporteModel();
        modelo.setTipoReporte(request.getTipoReporte().toUpperCase());
        modelo.setFechaInicio(request.getFechaInicio());
        modelo.setFechaFin(request.getFechaFin());
        modelo.setTotalRecaudado(recaudacionTotal);
        modelo.setTotalReservas(cantidadReservas);
        modelo.setCanchaEstrella(canchaEstrella);
        modelo.setGeneradoPor(request.getGeneradoPor()); 
        // 💡 Nota: fechaGeneracion se omite aquí porque la maneja automáticamente el @PrePersist de la entidad.

        ReporteModel registroGuardado = reporteRepository.save(modelo);

        // 4. 📤 Mapeo limpio hacia el DTO de respuesta final
        DtoReporteResponse response = new DtoReporteResponse();
        response.setIdReporte(registroGuardado.getId());
        response.setTipoReporte(registroGuardado.getTipoReporte());
        response.setFechaInicio(registroGuardado.getFechaInicio());
        response.setFechaFin(registroGuardado.getFechaFin());
        response.setTotalRecaudado(registroGuardado.getTotalRecaudado());
        response.setTotalReservas(registroGuardado.getTotalReservas());
        response.setCanchaEstrella(registroGuardado.getCanchaEstrella());
        response.setFechaGeneracion(registroGuardado.getFechaGeneracion()); // Trae el valor generado por la DB
        response.setGeneradoPor(registroGuardado.getGeneradoPor());

        return response;
    }
}