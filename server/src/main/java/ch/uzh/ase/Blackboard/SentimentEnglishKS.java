package ch.uzh.ase.Blackboard;

import ch.uzh.ase.Util.Tweet;
import com.neovisionaries.i18n.CountryCode;

import java.util.List;

/**
 * Created by jonas on 25.04.2017.
 */
public class SentimentEnglishKS implements IKS, IKSMaster {
    @Override
    public boolean execCondition(Tweet tweet) {

        //TODO jwa clarify which country code is used for english
        if (tweet.getIso() != null && (tweet.getIso() == CountryCode.GB || tweet.getIso() == CountryCode.US)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void execAction(Tweet tweet) {

    }

    @Override
    public void splitWork() {

    }

    @Override
    public List<IKSSlave> callSlaves() {
        return null;
    }

    @Override
    public void service() {

    }
}
