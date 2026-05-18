package cl.municipalidad.reportes.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.time.LocalDate;

@Component
public class AnaliticaClient {

    private final WebClient webClientPagos;
    private final WebClient webClientReservas;

    // Spring asocia automáticamente los nombres de los parámetros con los Beans de WebClientConfig
    public AnaliticaClient(WebClient webClientPagos, WebClient webClientReservas) {
        this.webClientPagos = webClientPagos;
        this.webClientReservas = webClientReservas;
    }

    /**
     * Consume ms-pagos para obtener la suma total recaudada en un rango de fechas.
     * Si falla, retorna 0.0 de manera segura (fallback tolerante a fallos).
     */
    public Mono<Double> obtenerRecaudacionPorRango(LocalDate inicio, LocalDate fin) {
        return webClientPagos.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/pagos/analitica/recaudacion")
                        .queryParam("fechaInicio", inicio)
                        .queryParam("fechaFin", fin)
                        .build())
                .retrieve()
                .bodyToMono(Double.class)
                .onErrorReturn(0.0);
    }

    /**
     * Consume ms-reservas para obtener el total de reservas confirmadas en un rango de fechas.
     * Si falla, retorna 0 de manera segura.
     */
    public Mono<Integer> obtenerTotalReservasPorRango(LocalDate inicio, LocalDate fin) {
        return webClientReservas.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/reservas/analitica/conteo")
                        .queryParam("fechaInicio", inicio)
                        .queryParam("fechaFin", fin)
                        .build())
                .retrieve()
                .bodyToMono(Integer.class)
                .onErrorReturn(0);
    }
}