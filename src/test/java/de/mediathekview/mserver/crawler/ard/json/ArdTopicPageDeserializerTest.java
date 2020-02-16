package de.mediathekview.mserver.crawler.ard.json;

import com.google.gson.JsonElement;
import de.mediathekview.mserver.crawler.ard.ArdFilmInfoDto;
import de.mediathekview.mserver.crawler.ard.ArdTopicInfoDto;
import de.mediathekview.mserver.testhelper.JsonFileReader;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.net.URLEncoder;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ArdTopicPageDeserializerTest {
  @Test
  public void testDeserialize() {
    final JsonElement jsonElement = JsonFileReader.readJson("/ard/ard_topic.json");

    final ArdFilmInfoDto[] expected =
        new ArdFilmInfoDto[] {
          new ArdFilmInfoDto(
              "Y3JpZDovL2Rhc2Vyc3RlLmRlL2Flbm5hIGJ1cmRhL2IxZjY2NWQzLWIyOTYtNDQ4ZS05YmQ1LTk5MWUzZTcxZWNkMQ",
              "https://api.ardmediathek.de/public-gateway?variables="
                  + URLEncoder.encode(
                      "{\"client\":\"ard\",\"clipId\":\"Y3JpZDovL2Rhc2Vyc3RlLmRlL2Flbm5hIGJ1cmRhL2IxZjY2NWQzLWIyOTYtNDQ4ZS05YmQ1LTk5MWUzZTcxZWNkMQ\",\"deviceType\":\"pc\"}",
                      UTF_8)
                  + "&extensions="
                  + URLEncoder.encode(
                      "{\"persistedQuery\":{\"version\":1,\"sha256Hash\":\"38e4c23d15b4b007e2e31068658944f19797c2fb7a75c93bc0a77fe1632476c6\"}}",
                      UTF_8),
              0),
          new ArdFilmInfoDto(
              "Y3JpZDovL2Rhc2Vyc3RlLmRlL2Flbm5hIGJ1cmRhLzdkMWRhYzhmLWMxOWEtNDdjZS1hNDBmLTgzN2NjZTRhNTNjZQ",
              "https://api.ardmediathek.de/public-gateway?variables="
                  + URLEncoder.encode(
                      "{\"client\":\"ard\",\"clipId\":\"Y3JpZDovL2Rhc2Vyc3RlLmRlL2Flbm5hIGJ1cmRhLzdkMWRhYzhmLWMxOWEtNDdjZS1hNDBmLTgzN2NjZTRhNTNjZQ\",\"deviceType\":\"pc\"}",
                      UTF_8)
                  + "&extensions="
                  + URLEncoder.encode(
                      "{\"persistedQuery\":{\"version\":1,\"sha256Hash\":\"38e4c23d15b4b007e2e31068658944f19797c2fb7a75c93bc0a77fe1632476c6\"}}",
                      UTF_8),
              0),
          new ArdFilmInfoDto(
              "Y3JpZDovL2Rhc2Vyc3RlLmRlL2Flbm5hIGJ1cmRhL2I0MWQ3MDhjLTMwNDItNGNhYy1iOTMyLTI0NzNlNjdiM2MwOQ",
              "https://api.ardmediathek.de/public-gateway?variables="
                  + URLEncoder.encode(
                      "{\"client\":\"ard\",\"clipId\":\"Y3JpZDovL2Rhc2Vyc3RlLmRlL2Flbm5hIGJ1cmRhL2I0MWQ3MDhjLTMwNDItNGNhYy1iOTMyLTI0NzNlNjdiM2MwOQ\",\"deviceType\":\"pc\"}",
                      UTF_8)
                  + "&extensions="
                  + URLEncoder.encode(
                      "{\"persistedQuery\":{\"version\":1,\"sha256Hash\":\"38e4c23d15b4b007e2e31068658944f19797c2fb7a75c93bc0a77fe1632476c6\"}}",
                      UTF_8),
              0),
          new ArdFilmInfoDto(
              "Y3JpZDovL2Rhc2Vyc3RlLmRlL2Flbm5hIGJ1cmRhLzhiMzE1ZTlkLWFmZjktNDE2Mi1iNzk1LWY3OTk3MGJjYWZkMw",
              "https://api.ardmediathek.de/public-gateway?variables="
                  + URLEncoder.encode(
                      "{\"client\":\"ard\",\"clipId\":\"Y3JpZDovL2Rhc2Vyc3RlLmRlL2Flbm5hIGJ1cmRhLzhiMzE1ZTlkLWFmZjktNDE2Mi1iNzk1LWY3OTk3MGJjYWZkMw\",\"deviceType\":\"pc\"}",
                      UTF_8)
                  + "&extensions="
                  + URLEncoder.encode(
                      "{\"persistedQuery\":{\"version\":1,\"sha256Hash\":\"38e4c23d15b4b007e2e31068658944f19797c2fb7a75c93bc0a77fe1632476c6\"}}",
                      UTF_8),
              0),
          new ArdFilmInfoDto(
              "Y3JpZDovL2Rhc2Vyc3RlLmRlL2Flbm5hIGJ1cmRhLzk2OTI1NTQxLWRkZTQtNGJhOS1hZTUyLTQ0MDUwYzIwOGVlOA",
              "https://api.ardmediathek.de/public-gateway?variables="
                  + URLEncoder.encode(
                      "{\"client\":\"ard\",\"clipId\":\"Y3JpZDovL2Rhc2Vyc3RlLmRlL2Flbm5hIGJ1cmRhLzk2OTI1NTQxLWRkZTQtNGJhOS1hZTUyLTQ0MDUwYzIwOGVlOA\",\"deviceType\":\"pc\"}",
                      UTF_8)
                  + "&extensions="
                  + URLEncoder.encode(
                      "{\"persistedQuery\":{\"version\":1,\"sha256Hash\":\"38e4c23d15b4b007e2e31068658944f19797c2fb7a75c93bc0a77fe1632476c6\"}}",
                      UTF_8),
              0),
          new ArdFilmInfoDto(
              "Y3JpZDovL2Rhc2Vyc3RlLmRlL2Flbm5hIGJ1cmRhLzhjYTk2MmFiLTgyOTctNDE4Yy05YzUxLTdlN2Y1Mjg4ZWY5ZS9hdWRpb2Rlc2tyaXB0aW9u",
              "https://api.ardmediathek.de/public-gateway?variables="
                  + URLEncoder.encode(
                      "{\"client\":\"ard\",\"clipId\":\"Y3JpZDovL2Rhc2Vyc3RlLmRlL2Flbm5hIGJ1cmRhLzhjYTk2MmFiLTgyOTctNDE4Yy05YzUxLTdlN2Y1Mjg4ZWY5ZS9hdWRpb2Rlc2tyaXB0aW9u\",\"deviceType\":\"pc\"}",
                      UTF_8)
                  + "&extensions="
                  + URLEncoder.encode(
                      "{\"persistedQuery\":{\"version\":1,\"sha256Hash\":\"38e4c23d15b4b007e2e31068658944f19797c2fb7a75c93bc0a77fe1632476c6\"}}",
                      UTF_8),
              0)
        };

    final ArdTopicPageDeserializer instance = new ArdTopicPageDeserializer();

    final ArdTopicInfoDto ardTopicInfoDto = instance.deserialize(jsonElement, null, null);
    final Set<ArdFilmInfoDto> filmInfos = ardTopicInfoDto.getFilmInfos();

    assertThat(filmInfos.size(), equalTo(expected.length));
    assertThat(filmInfos, Matchers.containsInAnyOrder(expected));
  }
}
