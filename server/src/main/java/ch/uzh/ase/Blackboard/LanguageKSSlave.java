package ch.uzh.ase.Blackboard;

import ch.uzh.ase.Util.Tweet;
import com.neovisionaries.i18n.LanguageCode;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Silvio Fankhauser on 29.04.2017.
 */
public class LanguageKSSlave extends Thread implements  IKSSlave{

    private final LinkedBlockingQueue<Tweet> taskQueue = new LinkedBlockingQueue<>();
    private final AbstractKSMaster master;
    private  boolean shutdown = false;
    private static final Logger LOG = LoggerFactory.getLogger(LanguageKSSlave.class);

    public LanguageKSSlave(AbstractKSMaster master) {
        this.master = master;
    }

    @Override
    public void run() {

        while (!shutdown){

            Tweet nextTweet = taskQueue.poll();
            nextTweet.setStartLangDetection(DateTime.now());
            //TODO: implement Language detection
            nextTweet.setIso(LanguageCode.en);
            nextTweet.setEndLangDetection(DateTime.now());
            master.reportResult(nextTweet);
        }
    }

    @Override
    public void subservice(List<Tweet> tasks) {
        taskQueue.addAll(tasks);
    }

    @Override
    public int getUncompletedTasks() {
        return taskQueue.size();
    }

    @Override
    public void kill() {
        LOG.warn("Slave shutdown initiated!");
        this.shutdown = true;
    }
}
