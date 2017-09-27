package mServer.crawler.sender.hr;

import de.mediathekview.mlib.Const;
import de.mediathekview.mlib.daten.DatenFilm;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HrSendungDeserializer {
    
    private static final String QUERY_BROADCAST = "li.c-airdates__entry";
    private static final String QUERY_DESCRIPTION = "p.copytext__text";
    private static final String QUERY_TITLE = "p.c-programHeader__subline";
    private static final String HTML_TAG_SOURCE = "source";
    private static final String HTML_TAG_STRONG = "strong";
    private static final String HTML_TAG_TIME = "time";
    private static final String HTML_TAG_VIDEO = "video";
    private static final String HTML_ATTRIBUTE_DATETIME = "datetime";
    private static final String HTML_ATTRIBUTE_DURATION = "data-duration";
    private static final String HTML_ATTRIBUTE_SRC = "src";

    private final DateTimeFormatter dateFormatHtml = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mmZ");
    private final DateTimeFormatter dateFormatDatenFilm = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final DateTimeFormatter timeFormatDatenFilm = DateTimeFormatter.ofPattern("HH:mm:ss");

    public DatenFilm deserialize(String theme, String documentUrl, Document document) {
        
        String videoUrl = getVideoUrl(document);
        if (videoUrl == null || videoUrl.isEmpty()) {
            return null;
        }
        
        String broadcast = getBroadcast(document);
        LocalDateTime d = LocalDateTime.parse(broadcast, dateFormatHtml);
        String date = d.format(dateFormatDatenFilm);
        String time = d.format(timeFormatDatenFilm);
        String title = getTitle(document);
        long duration = getDuration(document);
        String description = getDescription(document);
        
        DatenFilm film = new DatenFilm(Const.HR, theme, documentUrl, title, videoUrl, "", date, time, duration, description);
        
        return film;
    }
    
    private String getBroadcast(Document document) {
        String broadcast = "";
        
        Element broadcastElement = document.select(QUERY_BROADCAST).first();
        
        Elements children = broadcastElement.children();

        for(int j = 0; j < children.size(); j++) {
            Element child = children.get(j);

            if(child.tagName().compareToIgnoreCase(HTML_TAG_TIME) == 0) {
                broadcast = child.attr(HTML_ATTRIBUTE_DATETIME);
            }
        }
        
        return broadcast;
    }
    
    private String getDescription(Document document) {
        String desc = "";
        
        Element descElement = document.select(QUERY_DESCRIPTION).first();
        if(descElement != null) {
            Elements children = descElement.children();
            if(children.size() > 0) {
                for (int i = 0; i < children.size(); i++) {
                    if (children.get(i).tagName().compareToIgnoreCase(HTML_TAG_STRONG) == 0) {
                        desc = children.get(i).text();
                    }
                }
            } else {
                desc = descElement.text();
            }
        }
        
        return desc;
    }
    
    private long getDuration(Document document) {
        String duration = "";
        
        Element durationElement = document.select(HTML_TAG_VIDEO).first();
        if (durationElement != null) {
            duration = durationElement.attr(HTML_ATTRIBUTE_DURATION);
        }
        
        if (duration != null && !duration.isEmpty()) {
            return Long.parseLong(duration);
        } 
        return 0;
    }
    
    private String getTitle(Document document) {
        String title = "";
        
        Element titleElement = document.select(QUERY_TITLE).first();
        if(titleElement != null) {
            title = titleElement.text();
        }
        
        return title;
    }
    
        private String getVideoUrl(Document document) {
        String url = "";
        
        Element urlElement = document.select(HTML_TAG_SOURCE).first();
        if(urlElement != null) {
            url = urlElement.attr(HTML_ATTRIBUTE_SRC);
        }
        
        return url;
    }
}
