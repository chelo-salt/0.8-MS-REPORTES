package cl.municipalidad.reportes.controller;

import cl.municipalidad.reportes.dto.request.DtoGenerarReporteRequest;
import cl.municipalidad.reportes.dto.response.DtoReporteResponse;
import cl.municipalidad.reportes.service.ReporteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    @PostMapping("/generar")
    public ResponseEntity<DtoReporteResponse> crearBalanceAnalitico(
            @Valid @RequestBody DtoGenerarReporteRequest request,
            @RequestHeader("Authorization") String token) { // 🔑 Captura el JWT que el Gateway ya validó y propagó
        
        // 🔄 Enviamos los datos y el token para la orquestación síncrona/reactiva
        DtoReporteResponse response = reporteService.procesarYGuardarReporte(request, token);
        
        // 🚀 Retornamos un 201 Created en cumplimiento con las buenas prácticas de diseño RESTful
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}