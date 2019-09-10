package de.mediathekview.mserver.crawler.mdr.parser;

import de.mediathekview.mserver.crawler.basic.CrawlerUrlDTO;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;
import java.util.Set;

import static de.mediathekview.mserver.base.HtmlConsts.ATTRIBUTE_HREF;

public class MdrLetterPageDeserializer {

  private static final String TOPIC_URL_SELECTOR = "div.cssBroadcastSeries a";

  private final String baseUrl;

  public MdrLetterPageDeserializer(final String aBaseUrl) {
    baseUrl = aBaseUrl;
  }

  public Set<CrawlerUrlDTO> deserialize(final Document aDocument) {
    final Set<CrawlerUrlDTO> letterPages = new HashSet<>();

    final Elements dayLinks = aDocument.select(TOPIC_URL_SELECTOR);
    for (final Element dayLink : dayLinks) {
      final String link = dayLink.attr(ATTRIBUTE_HREF);
      letterPages.add(new CrawlerUrlDTO(baseUrl + link));
    }

    return letterPages;
  }
}