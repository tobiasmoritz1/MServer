package de.mediathekview.mserver.crawler.ard.json;

import com.google.gson.*;
import de.mediathekview.mserver.base.utils.JsonUtils;
import de.mediathekview.mserver.crawler.ard.ArdFilmInfoDto;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ArdDayPageDeserializer extends ArdTeasersDeserializer
    implements JsonDeserializer<Set<ArdFilmInfoDto>> {

  private static final String ELEMENT_DATA = "data";
  private static final String ELEMENT_PROGRAM_PAGE = "programPage";
  private static final String ELEMENT_WIDGETS = "widgets";
  private static final String ELEMENT_TEASERS = "teasers";

  @Override
  public Set<ArdFilmInfoDto> deserialize(
          final JsonElement jsonElement, final Type type, final JsonDeserializationContext context) {
    final Set<ArdFilmInfoDto> results = new HashSet<>();

    if (JsonUtils.checkTreePath(
        jsonElement, Optional.empty(), ELEMENT_DATA, ELEMENT_PROGRAM_PAGE, ELEMENT_WIDGETS)) {
      final JsonArray widgets =
          jsonElement
              .getAsJsonObject()
              .get(ELEMENT_DATA)
              .getAsJsonObject()
              .get(ELEMENT_PROGRAM_PAGE)
              .getAsJsonObject()
              .get(ELEMENT_WIDGETS)
              .getAsJsonArray();
      for (final JsonElement widgetElement : widgets) {
        final JsonObject widgetObject = widgetElement.getAsJsonObject();
        if (widgetObject.has(ELEMENT_TEASERS)) {
          final JsonArray teasers = widgetObject.get(ELEMENT_TEASERS).getAsJsonArray();
          results.addAll(parseTeasers(teasers));
        }
      }
    }

    return results;
  }
}
