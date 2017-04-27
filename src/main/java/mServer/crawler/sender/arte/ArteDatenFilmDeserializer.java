package mServer.crawler.sender.arte;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoField;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mServer.crawler.CrawlerTool;
import mServer.crawler.sender.newsearch.Qualities;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import de.mediathekview.mlib.Const;
import de.mediathekview.mlib.daten.DatenFilm;
import de.mediathekview.mlib.daten.ListeFilme;
import de.mediathekview.mlib.tool.MVHttpClient;

public class ArteDatenFilmDeserializer implements JsonDeserializer<ListeFilme>
{
    private static final Logger LOG = LogManager.getLogger(ArteDatenFilmDeserializer.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.GERMANY);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(Locale.GERMANY);
    private static final String JSON_OBJECT_KEY_PROGRAM = "program";
    private static final String JSON_ELEMENT_KEY_TITLE = "title";
    private static final String JSON_ELEMENT_KEY_SUBTITLE = "subtitle";
    private static final String JSON_ELEMENT_KEY_URL = "url";
    private static final String JSON_ELEMENT_KEY_PROGRAM_ID = "programId";
    private static final String ARTE_VIDEO_INFORMATION_URL_PATTERN = "https://api.arte.tv/api/player/v1/config/%s/%s";
    private static final String ARTE_VIDEO_INFORMATION_URL_COUNTRY_GERMANY = "de";
    private static final String JSON_ELEMENT_KEY_SHORT_DESCRIPTION = "shortDescription";
    
    //1. Non-Capture Group duration, 2. Positive lookbehind for 0 or more non word charackters, 3. One or more numbers, 4. positive lookahed closing span tag
    private static final String REGEX_PATTERN_DURATION = "(?!:<span class=\"duration\">)(?<=\\W*)\\d+(?=\\s*\\w*\\W*<\\/span>)";
    private static final String REGEX_PATTERN_DATETIME = "(?!:<li class=\"main-programming-broadcast-date\">)(?<=\\W*)\\w*,\\s*\\d+\\.\\s\\w+\\s*\\w+\\s*\\d*\\.\\d*\\s*\\w+(?=\\s*<\\/li>)";

    @Override
    public ListeFilme deserialize(JsonElement aJsonElement, Type aType, JsonDeserializationContext aContext) throws JsonParseException
    {
        ListeFilme listeFilme = new ListeFilme();

        for (JsonElement jsonElement : aJsonElement.getAsJsonArray())
        {
            DatenFilm datenFilm = elementToFilm(jsonElement.getAsJsonObject());
            if (null != datenFilm)
            {
                listeFilme.add(datenFilm);
            }

        }

        return listeFilme;
    }

    private DatenFilm elementToFilm(JsonObject aJsonObject)
    {
        DatenFilm film = null;
        if(aJsonObject != null && aJsonObject.has(JSON_OBJECT_KEY_PROGRAM))
        {
            JsonObject programObject = aJsonObject.get(JSON_OBJECT_KEY_PROGRAM).getAsJsonObject();
            if(programObject.has(JSON_ELEMENT_KEY_TITLE) && programObject.has(JSON_ELEMENT_KEY_PROGRAM_ID) && programObject.has(JSON_ELEMENT_KEY_URL))
            {
                String thema = !programObject.get(JSON_ELEMENT_KEY_TITLE).isJsonNull() ? programObject.get(JSON_ELEMENT_KEY_TITLE).getAsString() : "";
                String titel = !programObject.get(JSON_ELEMENT_KEY_SUBTITLE).isJsonNull() ? programObject.get(JSON_ELEMENT_KEY_SUBTITLE).getAsString() : "";
                String beschreibung = !programObject.get(JSON_ELEMENT_KEY_SHORT_DESCRIPTION).isJsonNull() ? programObject.get(JSON_ELEMENT_KEY_SHORT_DESCRIPTION).getAsString() : "";
            
                String urlWeb = programObject.get(JSON_ELEMENT_KEY_URL).getAsString();
    
                //https://api.arte.tv/api/player/v1/config/[language:de/fr]/[programId]
                String programId = programObject.get(JSON_ELEMENT_KEY_PROGRAM_ID).getAsString();
                String videosUrl = String.format(ARTE_VIDEO_INFORMATION_URL_PATTERN, ARTE_VIDEO_INFORMATION_URL_COUNTRY_GERMANY, programId);
    
                Gson gson = new GsonBuilder().registerTypeAdapter(ArteVideoDTO.class, new ArteVideoDeserializer()).create();
    
                OkHttpClient httpClient = MVHttpClient.getInstance().getHttpClient();
                Request requestVideoDetails = new Request.Builder().url(videosUrl).build();
                
                Request requestFilmDetails = new Request.Builder().url(urlWeb).build();
    
                try
                {
                    Response responseVideoDetails = httpClient.newCall(requestVideoDetails).execute();
                    Response responseFilmDetails = httpClient.newCall(requestFilmDetails).execute();
                    if(responseFilmDetails.isSuccessful() && responseVideoDetails.isSuccessful())
                    {
                        ArteVideoDTO video = gson.fromJson(responseVideoDetails.body().string(), ArteVideoDTO.class);
                        
                        LocalDateTime dateTime = readDateTime(responseFilmDetails);
                        
                        //The duration as time so it can be formatted and co.
                        LocalTime durationAsTime = readDuration(responseFilmDetails);
            
                        film = new DatenFilm(Const.ARTE_DE, thema, urlWeb, titel, video.getUrl(Qualities.NORMAL), "" /*urlRtmp*/,
                                dateTime.format(DATE_FORMATTER), dateTime.format(TIME_FORMATTER), durationAsTime.toSecondOfDay(), beschreibung);
                        if (video.getVideoUrls().containsKey(Qualities.HD))
                        {
                            CrawlerTool.addUrlHd(film, video.getUrl(Qualities.HD), "");
                        }
                        if (video.getVideoUrls().containsKey(Qualities.SMALL))
                        {
                            CrawlerTool.addUrlKlein(film, video.getUrl(Qualities.SMALL), "");
                        }
                    }

                } catch (IOException ioException)
                {
                    LOG.error("Beim laden der Informationen eines Filmes für Arte kam es zu Verbindungsproblemen.", ioException);
                }
            }

        }
        return film;
    }
    
    private LocalDateTime readDateTime(Response aResponse) throws IOException
    {
        DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder().appendPattern("EEEE', 'dd'. 'MMMM' um 'H.mm' Uhr'")
            .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
            .parseDefaulting(ChronoField.YEAR, 2017)
            .toFormatter(Locale.GERMANY); 
            
            Matcher matcher = Pattern.compile(REGEX_PATTERN_DATETIME).matcher(aResponse.body().string());
        if(matcher.find())
        {
            LocalDateTime localDateTime = LocalDateTime.parse(matcher.group(), dateTimeFormatter);
            return localDateTime;
        } else {
            return LocalDateTime.of(LocalDate.now(),LocalTime.MIDNIGHT);
        }
        
        
    }
    
    private LocalTime readDuration(Response aResponse) throws IOException
    {
        LocalTime localTime = LocalTime.MIN;
        
        Matcher matcher = Pattern.compile(REGEX_PATTERN_DURATION).matcher(aResponse.body().string());
        if(matcher.find())
        {
            long durationInMinutes = Long.parseLong(matcher.group());
            localTime = localTime.plusMinutes(durationInMinutes);
        }
        
        return localTime;
    }
}
