package penelope.corretagem.calendryintegration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record EventLocation(
  String type,
  String location, // Usado para local físico/customizado

  @JsonProperty("join_url") // Usado para reuniões online
  String joinUrl
) {}
