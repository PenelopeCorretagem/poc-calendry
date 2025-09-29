package penelope.corretagem.calendryintegration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

// Corpo da requisição para criar o link
public record SchedulingLinkRequest(
  @JsonProperty("max_event_count")
  int maxEventCount,

  String owner, // URI do EventType

  @JsonProperty("owner_type")
  String ownerType
) {
  // Construtor de conveniência para um link de uso único
  public SchedulingLinkRequest(String ownerUri) {
    this(1, ownerUri, "EventType");
  }
}