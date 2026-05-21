package cl.municipalidad.reportes.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.time.LocalDate;

@Component
public class AnaliticaClient {

    private final WebClient webClientPagos;
    private final WebClient webClientReservas;

    public AnaliticaClient(WebClient webClientPagos, WebClient webClientReservas) {
        this.webClientPagos = webClientPagos;
        this.webClientReservas = webClientReservas;
    }

    /**
     * Consume ms-pagos para obtener la suma total recaudada en un rango de fechas.
     */
    public Mono<Double> obtenerRecaudacionPorRango(LocalDate inicio, LocalDate fin, String token) {
        String tokenLimpio = token.startsWith("Bearer ") ? token : "Bearer " + token;

        return webClientPagos.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/pagos/pago/analitica/recaudacion") // 👈 Ruta ajustada al controlador
                        .queryParam("fechaInicio", inicio)
                        .queryParam("fechaFin", fin)
                        .build())
                .header("Authorization", tokenLimpio)
                .retrieve()
                .bodyToMono(Double.class)
                .doOnError(e -> System.err.println("🚨 ERROR EN MS-PAGOS: " + e.getMessage()))
                .onErrorReturn(0.0);
    }

    /**
     * Consume ms-reservas para obtener el total de reservas confirmadas.
     */
    public Mono<Integer> obtenerTotalReservasPorRango(LocalDate inicio, LocalDate fin, String token) {
        String tokenLimpio = token.startsWith("Bearer ") ? token : "Bearer " + token;

        return webClientReservas.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/reservas/analitica/conteo")
                        .queryParam("fechaInicio", inicio)
                        .queryParam("fechaFin", fin)
                        .build())
                .header("Authorization", tokenLimpio)
                .retrieve()
                .bodyToMono(Integer.class)
                .doOnError(e -> System.err.println("🚨 ERROR EN MS-RESERVAS (Conteo): " + e.getMessage()))
                .onErrorReturn(0);
    }

    /**
     * Consume ms-reservas para conocer la cancha más solicitada.
     */
    public Mono<String> obtenerCanchaEstrellaPorRango(LocalDate inicio, LocalDate fin, String token) {
        String tokenLimpio = token.startsWith("Bearer ") ? token : "Bearer " + token;

        return webClientReservas.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/reservas/analitica/cancha-estrella")
                        .queryParam("fechaInicio", inicio)
                        .queryParam("fechaFin", fin)
                        .build())
                .header("Authorization", tokenLimpio)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(e -> System.err.println("🚨 ERROR EN MS-RESERVAS (Estrella): " + e.getMessage()))
                .onErrorReturn("Sin datos");
    }
}