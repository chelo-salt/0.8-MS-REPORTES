package cl.municipalidad.reportes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClientPagos() {
        return WebClient.builder()
                .baseUrl("http://localhost:8082") // 💰 Conectores directos a ms-pagos
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Bean
    public WebClient webClientReservas() {
        return WebClient.builder()
                .baseUrl("http://localhost:8083") // ⚽ Conectores directos a ms-reservas
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}