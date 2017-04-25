package ch.uzh.ase.Util;

import com.neovisionaries.i18n.CountryCode;

/**
 * Created by jonas on 24.04.2017.
 */
public class Tweet {

    CountryCode iso = null;
    Sentiment sentiment = null;


    public CountryCode getIso() {
        return this.iso;
    }
    public void setIso(CountryCode iso) {
        this.iso = iso;
    }

    public Sentiment getSentiment() {
        return this.sentiment;
    }
    public void setSentiment(Sentiment sentiment) {
        this.sentiment = sentiment;
    }
}

