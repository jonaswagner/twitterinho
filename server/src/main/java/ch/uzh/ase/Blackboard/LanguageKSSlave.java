package ch.uzh.ase.Blackboard;

import ch.uzh.ase.Util.Tweet;
import com.neovisionaries.i18n.LanguageCode;
import org.apache.tika.langdetect.OptimaizeLangDetector;
import org.apache.tika.language.detect.LanguageDetector;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class LanguageKSSlave extends Thread implements IKSSlave {

    private static final Logger LOG = LoggerFactory.getLogger(LanguageKSSlave.class);
    private final LinkedBlockingQueue<Tweet> taskQueue = new LinkedBlockingQueue<Tweet>();
    private final AbstractKSMaster master;
    private boolean shutdown = false;
    private final LanguageDetector detector = new OptimaizeLangDetector().loadModels();

    public LanguageKSSlave(final AbstractKSMaster master) throws IOException {
        this.master = master;
    }



    @Override
    public void run() {
        while (!shutdown) {
            if (!taskQueue.isEmpty()) {
                Tweet nextTweet = taskQueue.poll();
                nextTweet.setStartLangDetection(DateTime.now());
                String text = nextTweet.getText();

                String languageCode = detector.detect(text).getLanguage();
                nextTweet.setIso(LanguageCode.getByCode(languageCode));
                nextTweet.setEndLangDetection(DateTime.now());

                LOG.info(this.getName() + "; " +nextTweet.getText() + ", " + nextTweet.getIso().getName());
                master.reportResult(nextTweet);
            }
        }
    }

    @Override
    public void subservice(final List<Tweet> tasks) {
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
