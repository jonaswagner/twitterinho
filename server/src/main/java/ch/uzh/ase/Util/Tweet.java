package ch.uzh.ase.Util;

import com.neovisionaries.i18n.LanguageCode;
import org.joda.time.DateTime;

/**
 * Created by jonas on 24.04.2017.
 */
public class Tweet {

    private LanguageCode iso = null;
    private Sentiment sentiment = null;
    private String text = null;
    private String author = null;
    private DateTime date = null;
    private String searchID = null;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public String getSearchID() {
        return searchID;
    }

    public void setSearchID(String searchID) {
        this.searchID = searchID;
    }

    public LanguageCode getIso() {
        return this.iso;
    }

    public void setIso(LanguageCode iso) {
        this.iso = iso;
    }

    public Sentiment getSentiment() {
        return this.sentiment;
    }

    public void setSentiment(Sentiment sentiment) {
        this.sentiment = sentiment;
    }


}

