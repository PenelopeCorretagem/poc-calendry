package penelope.corretagem.calendryintegration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// Representa um único tipo de evento da sua conta
@JsonIgnoreProperties(ignoreUnknown = true)
public record EventType(
  String uri,
  String name
) {}