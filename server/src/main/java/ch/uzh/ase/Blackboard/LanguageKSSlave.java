package ch.uzh.ase.Blackboard;

import ch.uzh.ase.Util.Tweet;
import com.neovisionaries.i18n.LanguageCode;
import org.apache.tika.langdetect.OptimaizeLangDetector;
import org.apache.tika.language.detect.LanguageDetector;
import org.apache.tika.language.detect.LanguageResult;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Silvio Fankhauser on 29.04.2017.
 */
public class LanguageKSSlave extends Thread implements IKSSlave {

    private final LinkedBlockingQueue<Tweet> taskQueue = new LinkedBlockingQueue<Tweet>();
    private final AbstractKSMaster master;
    private boolean shutdown = false;
    private static final Logger LOG = LoggerFactory.getLogger(LanguageKSSlave.class);

    public LanguageKSSlave(AbstractKSMaster master) throws IOException {
        this.master = master;
    }
    LanguageDetector detector = new OptimaizeLangDetector().loadModels();


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
                master.reportResult(nextTweet);
            }
        }
    }

    @Override
    public void subservice(List<Tweet> tasks) {
        LOG.info(tasks.size() + " new tasks added to the taskList");
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
