package de.mediathekview.mserver.crawler.zdf;

import de.mediathekview.mlib.daten.Film;
import de.mediathekview.mlib.daten.FilmUrl;
import de.mediathekview.mlib.daten.GeoLocations;
import de.mediathekview.mlib.daten.Resolution;
import de.mediathekview.mserver.crawler.zdf.json.DownloadDto;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class DownloadDtoFilmConverter {

  private DownloadDtoFilmConverter() {}

  public static void addUrlsToFilm(
      final Film aFilm,
      final DownloadDto aDownloadDto,
      final Optional<ZdfVideoUrlOptimizer> aUrlOptimizer,
      final String aLanguage)
      throws MalformedURLException {

    for (final Map.Entry<Resolution, String> qualitiesEntry :
        aDownloadDto.getDownloadUrls(aLanguage).entrySet()) {
      String url = qualitiesEntry.getValue();

      if (qualitiesEntry.getKey() == Resolution.NORMAL && aUrlOptimizer.isPresent()) {
        url = aUrlOptimizer.get().getOptimizedUrlNormal(url);
      }

      aFilm.addUrl(qualitiesEntry.getKey(), new FilmUrl(url));
    }

    if (!aFilm.hasHD() && aUrlOptimizer.isPresent()) {
      final Optional<String> hdUrl =
          aUrlOptimizer.get().determineUrlHd(aFilm.getUrl(Resolution.NORMAL).toString());
      if (hdUrl.isPresent()) {
        aFilm.addUrl(Resolution.HD, new FilmUrl(hdUrl.get()));
      }
    }

    if (aDownloadDto.getSubTitleUrl().isPresent()) {
      aFilm.addSubtitle(new URL(aDownloadDto.getSubTitleUrl().get()));
    }

    if (aDownloadDto.getGeoLocation().isPresent()) {
      final Collection<GeoLocations> geo = new ArrayList<>();
      geo.add(aDownloadDto.getGeoLocation().get());
      aFilm.setGeoLocations(geo);
    }
  }
}