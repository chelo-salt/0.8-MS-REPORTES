package cl.municipalidad.reportes.controller;

import cl.municipalidad.reportes.dto.request.DtoGenerarReporteRequest;
import cl.municipalidad.reportes.dto.response.DtoReporteResponse;
import cl.municipalidad.reportes.service.ReporteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reportes")
@RequiredArgsConstructor // 🚀 Lombok inyecta el ReporteService por constructor de forma automática
public class ReporteController {

    private final ReporteService reporteService;

    @PostMapping("/generar")
    public ResponseEntity<DtoReporteResponse> crearBalanceAnalitico(
            @Valid @RequestBody DtoGenerarReporteRequest request) {
        
        // 🔄 Delegamos el cálculo a la capa de negocio y retornamos el consolidado
        DtoReporteResponse response = reporteService.procesarYGuardarReporte(request);
        return ResponseEntity.ok(response);
    }
}