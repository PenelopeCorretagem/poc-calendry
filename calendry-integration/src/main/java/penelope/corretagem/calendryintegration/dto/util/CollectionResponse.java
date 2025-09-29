package penelope.corretagem.calendryintegration.dto.util;

import java.util.List;

public record CollectionResponse<T>(
  List<T> collection,
  Pagination pagination
) {}
