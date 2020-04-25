package de.mediathekview.mserver.crawler.ard;

import de.mediathekview.mlib.daten.Film;
import de.mediathekview.mlib.daten.Sender;
import de.mediathekview.mlib.messages.listener.MessageListener;
import de.mediathekview.mserver.base.config.MServerConfigManager;
import de.mediathekview.mserver.base.messages.ServerMessages;
import de.mediathekview.mserver.crawler.ard.tasks.ArdDayPageTask;
import de.mediathekview.mserver.crawler.ard.tasks.ArdFilmDetailTask;
import de.mediathekview.mserver.crawler.ard.tasks.ArdTopicPageTask;
import de.mediathekview.mserver.crawler.ard.tasks.ArdTopicsOverviewTask;
import de.mediathekview.mserver.crawler.basic.AbstractCrawler;
import de.mediathekview.mserver.crawler.basic.CrawlerUrlDTO;
import de.mediathekview.mserver.progress.listeners.SenderProgressListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

public class ArdCrawler extends AbstractCrawler {

  private static final Logger LOG = LogManager.getLogger(ArdCrawler.class);

  public ArdCrawler(
      final ForkJoinPool aForkJoinPool,
      final Collection<MessageListener> aMessageListeners,
      final Collection<SenderProgressListener> aProgressListeners,
      final MServerConfigManager rootConfig) {
    super(aForkJoinPool, aMessageListeners, aProgressListeners, rootConfig);
  }

  @Override
  public Sender getSender() {
    return Sender.ARD;
  }

  private ConcurrentLinkedQueue<CrawlerUrlDTO> createDayUrlsToCrawl() {
    final ConcurrentLinkedQueue<CrawlerUrlDTO> dayUrlsToCrawl = new ConcurrentLinkedQueue<>();
    final LocalDateTime now = LocalDateTime.now();
    for (int i = 0; i <= crawlerConfig.getMaximumDaysForSendungVerpasstSection(); i++) {
      final String url =
          new ArdUrlBuilder(ArdConstants.BASE_URL, ArdConstants.DEFAULT_CLIENT)
              .addSearchDate(now.minusDays(i))
              .addSavedQuery(
                  ArdConstants.QUERY_DAY_SEARCH_VERSION, ArdConstants.QUERY_DAY_SEARCH_HASH)
              .build();

      dayUrlsToCrawl.offer(new CrawlerUrlDTO(url));
    }
    return dayUrlsToCrawl;
  }

  @Override
  protected RecursiveTask<Set<Film>> createCrawlerTask() {

    try {
      final Set<ForkJoinTask<Set<CrawlerUrlDTO>>> senderTopicTasks = createSenderTopicTasks();

      final ForkJoinTask<Set<ArdFilmInfoDto>> dayTask =
          forkJoinPool.submit(new ArdDayPageTask(this, createDayUrlsToCrawl()));

      final Set<CrawlerUrlDTO> senderTopicUrls = new HashSet<>();
      for (final ForkJoinTask<Set<CrawlerUrlDTO>> senderTopicTask : senderTopicTasks) {
        senderTopicUrls.addAll(senderTopicTask.get());
      }

      final Set<ArdFilmInfoDto> shows = dayTask.get();
      printMessage(
          ServerMessages.DEBUG_ALL_SENDUNG_FOLGEN_COUNT, getSender().getName(), shows.size());

      final ArdTopicPageTask topicTask =
          new ArdTopicPageTask(this, new ConcurrentLinkedQueue<>(senderTopicUrls));
      final int showsCountBefore = shows.size();
      shows.addAll(forkJoinPool.submit(topicTask).get());
      LOG.debug("ARD crawler found {} topics for all sub-sender.", shows.size() - showsCountBefore);

      printMessage(
          ServerMessages.DEBUG_ALL_SENDUNG_FOLGEN_COUNT, getSender().getName(), shows.size());
      getAndSetMaxCount(shows.size());
      return new ArdFilmDetailTask(this, new ConcurrentLinkedQueue(shows));
    } catch (final InterruptedException ex) {
      LOG.fatal("Exception in ARD crawler.", ex);
      Thread.currentThread().interrupt();
    } catch (final ExecutionException ex) {
      LOG.fatal("Exception in ARD crawler.", ex);
    }
    return null;
  }

  private Set<ForkJoinTask<Set<CrawlerUrlDTO>>> createSenderTopicTasks() {
    final Set<ForkJoinTask<Set<CrawlerUrlDTO>>> topicTasks = new HashSet<>();
    topicTasks.add(createTopicTaskBySender(ArdConstants.DEFAULT_CLIENT));
    topicTasks.add(createTopicTaskBySender("daserste"));
    topicTasks.add(createTopicTaskBySender("br"));
    topicTasks.add(createTopicTaskBySender("hr"));
    topicTasks.add(createTopicTaskBySender("mdr"));
    topicTasks.add(createTopicTaskBySender("ndr"));
    topicTasks.add(createTopicTaskBySender("radiobremen"));
    topicTasks.add(createTopicTaskBySender("rbb"));
    topicTasks.add(createTopicTaskBySender("sr"));
    topicTasks.add(createTopicTaskBySender("swr"));
    topicTasks.add(createTopicTaskBySender("wdr"));
    topicTasks.add(createTopicTaskBySender("one"));
    topicTasks.add(createTopicTaskBySender("alpha"));
    return topicTasks;
  }

  private ForkJoinTask<Set<CrawlerUrlDTO>> createTopicTaskBySender(final String sender) {
    final ArdTopicsOverviewTask topicsTask =
        new ArdTopicsOverviewTask(this, sender, createTopicsOverviewUrl(sender));

    return forkJoinPool.submit(topicsTask);
  }

  private ConcurrentLinkedQueue<CrawlerUrlDTO> createTopicsOverviewUrl(final String client) {
    final ConcurrentLinkedQueue<CrawlerUrlDTO> urls = new ConcurrentLinkedQueue<>();

    final String url =
        new ArdUrlBuilder(ArdConstants.BASE_URL, client)
            .addSavedQuery(ArdConstants.QUERY_TOPICS_VERSION, ArdConstants.QUERY_TOPICS_HASH)
            .build();

    urls.add(new CrawlerUrlDTO(url));

    return urls;
  }
}
