package penelope.corretagem.calendryintegration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CalendlyUser(
  String uri,
  String name,
  String email,

  @JsonProperty("scheduling_url")
  String schedulingUrl
) {}
