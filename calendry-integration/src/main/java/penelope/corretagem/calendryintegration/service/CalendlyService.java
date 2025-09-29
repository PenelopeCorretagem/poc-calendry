package penelope.corretagem.calendryintegration.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import penelope.corretagem.calendryintegration.dto.*;
import penelope.corretagem.calendryintegration.dto.util.CollectionResponse;
import penelope.corretagem.calendryintegration.dto.util.ResourceResponse;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

@Service
public class CalendlyService {
  private final WebClient webClient;

  public CalendlyService(WebClient calendlyWebClient) {
    this.webClient = calendlyWebClient;
  }

  /**
   * Busca o usuário autenticado e extrai sua URI.
   * Este método também serve para validar o token da API.
   * @return Um Mono contendo a URI do usuário como uma String.
   */
  public Mono<ResourceResponse<CalendlyUser>> getCurrentUser() {
    return this.webClient.get()
      .uri("/users/me")
      .retrieve()
      // Adiciona tratamento de erro específico para token inválido
      .onStatus(HttpStatus.UNAUTHORIZED::equals,
        response -> Mono.error(new RuntimeException("Token de API inválido ou expirado.")))
      // Converte o corpo da resposta para nosso modelo
      .bodyToMono(new ParameterizedTypeReference<>(){});
  }
  /**
   * Busca os eventos agendados para o usuário autenticado.
   * Orquestra as duas chamadas: primeiro busca a URI do usuário,
   * e então usa essa URI para buscar os eventos.
   * @return Um Mono com a coleção de eventos.
   */
  public Mono<CollectionResponse<ScheduledEvent>> getEventsForCurrentUser() {
    return getCurrentUser()
      .flatMap(user -> {
        // O código aqui dentro só executa após a URI do usuário ser obtida com sucesso
        System.out.println("URI do usuário obtida: " + user.resource().uri());
        return getScheduledEvents(user.resource().uri());
      });
  }

  /**
   * Busca uma lista de eventos agendados para uma URI de usuário específica.
   */
  private Mono<CollectionResponse<ScheduledEvent>> getScheduledEvents(String userUri) {
    return this.webClient.get()
      .uri(uriBuilder -> uriBuilder
        .path("/scheduled_events")
        .queryParam("user", userUri) // O parâmetro obrigatório é usado aqui
        .build())
      .retrieve()
      .bodyToMono(new ParameterizedTypeReference<>(){});
  }

  /**
   * Ponto de entrada principal para criar um link de agendamento a partir do nome do evento.
   */
  public Mono<ResourceResponse<BookingLink>> createSchedulingLinkByEventName(String eventName) {
    return findEventTypeUriByName(eventName)
      .flatMap(this::createSingleUseLinkForEventType);
  }

  /**
   * Encontra o URI de um tipo de evento específico pelo seu nome.
   */
  private Mono<String> findEventTypeUriByName(String eventName) {
    return getCurrentUser().flatMap(user ->
      this.webClient.get()
        .uri(uriBuilder -> uriBuilder
          .path("/event_types")
          .queryParam("user", user.resource().uri())
          .queryParam("active", "true")
          .build())
        .retrieve()
        .bodyToMono(new ParameterizedTypeReference<CollectionResponse<EventType>>() {})
        .flatMap(response -> response.collection().stream()
          .filter(eventType -> eventName.equalsIgnoreCase(eventType.name()))
          .findFirst()
          .map(eventType -> Mono.just(eventType.uri()))
          .orElse(Mono.error(new NoSuchElementException("Tipo de evento não encontrado: " + eventName)))
        )
    );
  }

  /**
   * Cria o link de uso único para um determinado URI de tipo de evento.
   */
  private Mono<ResourceResponse<BookingLink>> createSingleUseLinkForEventType(String eventTypeUri) {
    SchedulingLinkRequest requestBody = new SchedulingLinkRequest(eventTypeUri);

    return this.webClient.post()
      .uri("/scheduling_links")
      .bodyValue(requestBody)
      .retrieve()
      .bodyToMono(new ParameterizedTypeReference<>(){});
  }

  /**
   * Cria um evento avulso (One-off Event) diretamente no calendário.
   * @param request DTO com os detalhes do evento a ser criado.
   * @return A representação do evento que foi criado.
   */
  public Mono<ResourceResponse<ScheduledEvent>> createOneOffEvent(OneOffEventRequest request) {
    // Primeiro, obtemos o URI do usuário atual para incluir na requisição.
    return getCurrentUser().flatMap(user -> {
      OneOffEventRequest requestFormated = new OneOffEventRequest(
        request.name(),
        request.startTime(),
        request.endTime(),
        request.location(),
        request.invitees(),
        user.resource().uri() // Define o ownerUri com a URI do usuário atual
      );

      return this.webClient.post()
        .uri("/scheduled_events")
        .bodyValue(requestFormated)
        .retrieve()
        // Usamos seu DTO genérico para a resposta
        .bodyToMono(new ParameterizedTypeReference<>() {});
    });
  }
}
