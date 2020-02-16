package de.mediathekview.mserver.crawler.ard.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.mediathekview.mserver.base.utils.JsonUtils;
import de.mediathekview.mserver.crawler.ard.ArdFilmInfoDto;
import de.mediathekview.mserver.crawler.ard.ArdTopicInfoDto;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ArdTopicPageDeserializer extends ArdTeasersDeserializer
    implements JsonDeserializer<ArdTopicInfoDto> {

  private static final String ELEMENT_DATA = "data";
  private static final String ELEMENT_SHOW_PAGE = "showPage";
  private static final String ELEMENT_TEASERS = "teasers";
  private static final String ELEMENT_PAGE_NUMBER = "pageNumber";
  private static final String ELEMENT_TOTAL_ELEMENTS = "totalElements";
  private static final String ELEMENT_PAGINATION = "pagination";

  @Override
  public ArdTopicInfoDto deserialize(
      final JsonElement jsonElement, final Type type, final JsonDeserializationContext context) {
    final Set<ArdFilmInfoDto> results = new HashSet<>();
    final ArdTopicInfoDto ardTopicInfoDto = new ArdTopicInfoDto(results);

    if (JsonUtils.checkTreePath(
        jsonElement, Optional.empty(), ELEMENT_DATA, ELEMENT_SHOW_PAGE, ELEMENT_TEASERS)) {
      final JsonObject showPageElement =
          jsonElement
              .getAsJsonObject()
              .get(ELEMENT_DATA)
              .getAsJsonObject()
              .get(ELEMENT_SHOW_PAGE)
              .getAsJsonObject();
      final JsonElement teasersElement = showPageElement.get(ELEMENT_TEASERS);

      final JsonElement paginationElement = showPageElement.get(ELEMENT_PAGINATION);
      ardTopicInfoDto.setSubPageNumber(
          getChildElementAsIntOrNullIfNotExist(paginationElement, ELEMENT_PAGE_NUMBER));
      ardTopicInfoDto.setMaxSubPageNumber(
          getChildElementAsIntOrNullIfNotExist(paginationElement, ELEMENT_TOTAL_ELEMENTS));

      if (!teasersElement.isJsonNull()) {
        results.addAll(parseTeasers(teasersElement.getAsJsonArray()));
      }
    }

    return ardTopicInfoDto;
  }

  private int getChildElementAsIntOrNullIfNotExist(
      final JsonElement parentElement, final String childElementName) {
    if (parentElement == null || parentElement.isJsonNull()) {
      return 0;
    }
    return getJsonElementAsIntOrNullIfNotExist(
        parentElement.getAsJsonObject().get(childElementName));
  }

  private int getJsonElementAsIntOrNullIfNotExist(final JsonElement element) {
    if (element.isJsonNull()) {
      return 0;
    }
    return element.getAsInt();
  }
}
