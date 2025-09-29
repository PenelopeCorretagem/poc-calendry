package penelope.corretagem.calendryintegration.dto.util;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Pagination(
  int count,
  @JsonProperty("next_page") String nextPage,
  @JsonProperty("next_page_token") String nextPageToken
) {}
