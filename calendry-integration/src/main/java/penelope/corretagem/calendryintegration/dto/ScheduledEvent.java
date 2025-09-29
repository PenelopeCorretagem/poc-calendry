package penelope.corretagem.calendryintegration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record ScheduledEvent(
  String uri,
  String name,
  String status,

  @JsonProperty("start_time")
  Instant startTime,

  @JsonProperty("end_time")
  Instant endTime,

  EventLocation location
) {}
