package penelope.corretagem.calendryintegration.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import penelope.corretagem.calendryintegration.dto.BookingLink;
import penelope.corretagem.calendryintegration.dto.OneOffEventRequest;
import penelope.corretagem.calendryintegration.dto.util.CollectionResponse;
import penelope.corretagem.calendryintegration.dto.ScheduledEvent;
import penelope.corretagem.calendryintegration.dto.util.ResourceResponse;
import penelope.corretagem.calendryintegration.service.CalendlyService;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/calendry")
public class CalendryController {

  private final CalendlyService service;

  public CalendryController(CalendlyService service) {
    this.service = service;
  }

  /**
   * Endpoint para listar os eventos agendados do usuário autenticado.
   * @return Um Mono com a primeira página da coleção de eventos, que será
   * automaticamente serializado para JSON.
   */
  @GetMapping("/events")
  public Mono<ResponseEntity<CollectionResponse<ScheduledEvent>>> listCurrentUserEvents() {
    return this.service.getEventsForCurrentUser()
      .map(ResponseEntity::ok)
      .onErrorResume(NoSuchElementException.class, ex ->
        Mono.just(ResponseEntity.notFound().build())
      )
      .onErrorResume(Exception.class, ex ->
        Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build())
      );
  }

  @GetMapping("/create-link/{eventName}")
  public Mono<ResponseEntity<ResourceResponse<BookingLink>>> createLink(@PathVariable String eventName) {
    return this.service.createSchedulingLinkByEventName(eventName)
      .map(ResponseEntity::ok)
      .onErrorResume(NoSuchElementException.class, ex ->
        Mono.just(ResponseEntity.notFound().build())
      )
      .onErrorResume(Exception.class, ex ->
        Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build())
      );
  }

  @PostMapping("/create-one-off-event")
  public Mono<ResponseEntity<ResourceResponse<ScheduledEvent>>> createOneOffEvent(
    @RequestBody OneOffEventRequest request) {

    return service.createOneOffEvent(request)
      .map(createdEvent -> ResponseEntity.status(HttpStatus.CREATED).body(createdEvent))
      .onErrorResume(Exception.class, ex ->
        // Aqui você pode adicionar um tratamento de erro mais específico
        Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build())
      );
  }
}
