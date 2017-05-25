
package ch.uzh.ase.controller;

import ch.uzh.ase.Application;
import ch.uzh.ase.Blackboard.Blackboard;
import ch.uzh.ase.Monitoring.WorkloadObserver;
import ch.uzh.ase.TweetRetrieval.StreamRegistry;
import ch.uzh.ase.Util.Sentiment;
import ch.uzh.ase.Util.SystemWorkload;
import ch.uzh.ase.config.Configuration;
import ch.uzh.ase.Util.Term;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This is the REST interface of the Application.
 */
@RestController
public class MainController {

    /**
     * This method calls the database and returns all previously registered terms with their respective average sentiment value.
     * @return {@link List<Term>}
     */
    @RequestMapping(value = "/twt/terms", method = RequestMethod.GET)
    public ResponseEntity<List<Term>> getRegisteredTerms() {
        Map<String, Double> allRegistredTerms = Application.getDatabase().getAllAverageSentiments();
        List<Term> termList = new ArrayList<>();
        for (Map.Entry<String, Double> mapTerm : allRegistredTerms.entrySet()) {
            Term term = new Term(mapTerm.getKey(), mapTerm.getValue());
            termList.add(term);
        }
        return new ResponseEntity<List<Term>>(termList, HttpStatus.OK);
    }

    /**
     * deletes all documents for a certain searchId from DB
     */
    @RequestMapping(value = "/twt/term/{name}", method = RequestMethod.DELETE)
    public void deleteTerm(@PathVariable String name) {
        Application.getDatabase().deleteSearchIdEntries(name);
    }
    /**
     * deletes all documents and the respective collection in the DB
     */
    @RequestMapping(value = "/twt/terms", method = RequestMethod.DELETE)
    public void deleteCollection() {
        Application.getDatabase().deleteCollection();
    }

    /**
     * This method registers a new term and starts the {@link ch.uzh.ase.TweetRetrieval.TweetStream}, which retrieves the tweets.
     * @param name
     */
    @RequestMapping(value = "/twt/term/{name}", method = RequestMethod.POST)
    public void startStream(@PathVariable String name) {
        StreamRegistry.getInstance().register(name);
    }

    /**
     * This method stops the specific {@link ch.uzh.ase.TweetRetrieval.TweetStream}.
     * @param name
     */
    @RequestMapping(value = "/twt/term/{name}/stream", method = RequestMethod.PUT)
    public void stopStream(@PathVariable String name) {
        StreamRegistry.getInstance().locateStream(name).stopStream();
    }

    /**
     * This method calls the DB and retrieves the current average sentiment value of the current {@link Term}
     * @param name
     */
    @RequestMapping(value = "/twt/term/{name}/stream", method = RequestMethod.GET)
    public List<Double> getStream(@PathVariable String name) {
        double averageSentiment = Application.getDatabase().getAverageSentiment(name);
        List<Double> averages = new ArrayList<>();
        double currentSentiment = Application.getDatabase().getCurrentSentiment(name);
        averages.add(averageSentiment);
        averages.add(currentSentiment);
        return averages;
    }

    /**
     * This method returns live monitoring data of the system running the server.
     * @return {@link SystemWorkload}
     */
    @RequestMapping(value = "/twt/monitor", method = RequestMethod.GET)
    public ResponseEntity<SystemWorkload> getMonitorData() {
        SystemWorkload wl = WorkloadObserver.getInstance().getSystemWorkload();
        wl.addStatistics(Application.getDatabase().getTermStatistics());
        return new ResponseEntity<SystemWorkload>(wl, HttpStatus.OK);
    }


    /**
     * This method creates 100 additional artificial tweets.
     * @param term
     */
    @RequestMapping(value = "/twt/generate/{term}", method = RequestMethod.GET)
    public void generateArtificialTweets(@PathVariable String term) {
        Blackboard board = StreamRegistry.getInstance().getBlackBoard();
        int artificialTweetsNumber = Integer.valueOf(Configuration.getInstance().getProp().getProperty("artificialtweetsnumber"));
        for (int i = 0; i < artificialTweetsNumber; i++) {
            board.addNewTweets(Sentiment.generateTweets(artificialTweetsNumber, term));
        }
    }
}