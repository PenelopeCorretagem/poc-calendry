package penelope.corretagem.calendryintegration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

// O objeto 'resource' dentro da resposta da criação do link
public record BookingLink(
  @JsonProperty("booking_url")
  String bookingUrl
) {}

// A resposta completa será: ResourceResponse<BookingLink>