package penelope.corretagem.calendryintegration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

// Representa a requisição para criar um evento avulso
public record OneOffEventRequest(
  String name, // Nome do evento, ex: "Demonstração do Produto para a Empresa X"

  @JsonProperty("start_time")
  Instant startTime, // Data e hora de início em formato UTC

  @JsonProperty("end_time")
  Instant endTime, // Data e hora de fim em formato UTC

  // Localização pode ser customizada ou para videoconferência
  EventLocation location,

  // Lista de convidados (pode ter apenas um)
  List<Invitee> invitees,

  // Uri do User do sistema
  String ownerUri
) {}
