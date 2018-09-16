package de.mediathekview.mserver.crawler.wdr.tasks;

import static de.mediathekview.mserver.base.Consts.ATTRIBUTE_HREF;

import de.mediathekview.mserver.base.utils.UrlUtils;
import de.mediathekview.mserver.crawler.basic.AbstractCrawler;
import de.mediathekview.mserver.crawler.basic.AbstractDocumentTask;
import de.mediathekview.mserver.crawler.basic.AbstractRecrusivConverterTask;
import de.mediathekview.mserver.crawler.basic.CrawlerUrlDTO;
import de.mediathekview.mserver.crawler.wdr.WdrConstants;
import de.mediathekview.mserver.crawler.wdr.WdrTopicUrlDto;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.commons.text.WordUtils;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class WdrRadioPageTask extends AbstractDocumentTask<WdrTopicUrlDto, CrawlerUrlDTO> {

  private static final String SELECTOR_TOPIC = "h2 > a";

  public WdrRadioPageTask(
      AbstractCrawler aCrawler, ConcurrentLinkedQueue<CrawlerUrlDTO> aUrlToCrawlDtos) {
    super(aCrawler, aUrlToCrawlDtos);
  }

  @Override
  protected void processDocument(CrawlerUrlDTO aUrlDto, Document aDocument) {

    Elements topicElements = aDocument.select(SELECTOR_TOPIC);
    topicElements.forEach(
        topicElement -> {
          String url = topicElement.attr(ATTRIBUTE_HREF);
          String topic = topicElement.text();

          url = UrlUtils.addDomainIfMissing(url, WdrConstants.URL_BASE);
          topic = WordUtils.capitalize(topic);

          taskResults.add(new WdrTopicUrlDto(topic, url, false));
        });
  }

  @Override
  protected AbstractRecrusivConverterTask<WdrTopicUrlDto, CrawlerUrlDTO> createNewOwnInstance(
      ConcurrentLinkedQueue<CrawlerUrlDTO> aElementsToProcess) {
    return new WdrRadioPageTask(crawler, aElementsToProcess);
  }
}