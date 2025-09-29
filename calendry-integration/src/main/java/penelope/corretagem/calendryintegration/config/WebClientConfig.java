package penelope.corretagem.calendryintegration.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

  @Bean
  public WebClient calendlyWebClient(@Value("${calendly.api.base-url}") String baseUrl,
                                     @Value("${calendly.api.token}") String apiToken) {
    return WebClient.builder()
      .baseUrl(baseUrl) // Define a URL base
      .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiToken) // Define o cabeçalho de autenticação padrão
      .build(); // Constrói o objeto
  }
}
