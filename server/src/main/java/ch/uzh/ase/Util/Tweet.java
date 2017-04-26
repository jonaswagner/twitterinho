package ch.uzh.ase.Util;

import com.neovisionaries.i18n.LanguageCode;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by jonas on 24.04.2017.
 */
public class Tweet {

    public static final String TEST_SEARCH_TERM = "1234";
    public static final double INIT_SENTIMENT_SCORE = -1d;

    private LanguageCode iso = null;
    private double sentimentScore = INIT_SENTIMENT_SCORE;
    private String text = null;
    private String author = null;
    private DateTime date = null;
    private String searchID = null;

    public Tweet(String text, String author, DateTime date, String searchID) {
        this.text = text;
        this.author = author;
        this.date = date;
        this.searchID = searchID;
    }

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

    public double getSentimentScore() {
        return sentimentScore;
    }

    public void setSentimentScore(double sentimentScore) {
        this.sentimentScore = sentimentScore;
    }
}

