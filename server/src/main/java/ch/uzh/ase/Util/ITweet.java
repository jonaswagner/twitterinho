package ch.uzh.ase.Util;

import com.neovisionaries.i18n.CountryCode;

/**
 * Created by jonas on 24.04.2017.
 */
public interface ITweet {

    CountryCode iso = null;
    Sentiment sentiment = null;

    public CountryCode getIso();
    public void setIso(CountryCode loc);

    public Sentiment getSentiment();
    public void setSentiment(Sentiment sentiment);
}

