
package ch.uzh.ase.controller;

import ch.uzh.ase.Application;
import ch.uzh.ase.TweetRetrieval.StreamRegistry;
import ch.uzh.ase.domain.Sentiment;
import ch.uzh.ase.domain.Term;
import ch.uzh.ase.repository.SentimentRepository;
import com.sun.org.apache.bcel.internal.generic.ARRAYLENGTH;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.Path;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
public class MainController {


    @Autowired
    SentimentRepository sr;


    @RequestMapping(value = "/twt/terms", method = RequestMethod.GET)
    public ResponseEntity<List<Term>> getRegisteredTerms() {

        //String = searchId, Double = Sentimentvalue
        Map<String, Double> allRegistredTerms = Application.getDatabase().getAllAverageSentiments();
        List<Term> termList = new ArrayList<>();
        for (Map.Entry<String, Double> mapTerm : allRegistredTerms.entrySet()) {
            Term term = new Term(mapTerm.getKey(), mapTerm.getValue());
            termList.add(term);
        }
        return new ResponseEntity<List<Term>>(termList, HttpStatus.OK);
    }


    @RequestMapping(value = "/twt/term/{name}", method = RequestMethod.POST)
    public void addTerm(@PathVariable String name) {
        StreamRegistry.getInstance().register(name);
    }

    @RequestMapping(value = "/twt/term/{name}", method = RequestMethod.DELETE)
    public void deleteTerm(@PathVariable String name) {
        //deletes all documents for a certain searchId from DB
        Application.getDatabase().deleteSearchIdEntries(name);
    }

    //TODO Flavio: neue Methode
    @RequestMapping(value = "/twt/terms", method = RequestMethod.DELETE)
    public void deleteCollection() {
        //deletes whole DB
        Application.getDatabase().deleteCollection();
    }

    @RequestMapping(value = "/twt/term/{name}/stream/start", method = RequestMethod.POST)
    public void startStream(@PathVariable String name) {
        StreamRegistry.getInstance().register(name);
    }

    @RequestMapping(value = "/twt/term/{name}/stream", method = RequestMethod.GET)
    public double getStream(@PathVariable String name) {
        //holt den aktuellen durchschnittlichen Sentimentwert für eine searchId
        double averageSentiment = Application.getDatabase().getAverageSentiment(name);
        return Application.getDatabase().getAverageSentiment(name);
    }

    @RequestMapping(value = "/twt/term/{name}/stream", method = RequestMethod.PUT)
    public void cancelStream(@PathVariable String name) {
        StreamRegistry.getInstance().locateStream(name).stopStream();
    }

    @RequestMapping(value = "/twt/monitor/cpu", method = RequestMethod.GET)
    public ResponseEntity<Object> getCpuLoad() {
        return null;
    }

    @RequestMapping(value = "/twt/monitor/statistics", method = RequestMethod.GET)
    public ResponseEntity<Object> getTermStatistics() {
        Map<String, Long> termStats = Application.getDatabase().getTermStatistics();
        return null;
    }

    @RequestMapping(value = "/twt/monitor/work", method = RequestMethod.GET)
    public ResponseEntity<Object> getWorkLoad() {
        return null;
    }

    @RequestMapping(value = "/twt/monitor/slave", method = RequestMethod.GET)
    public ResponseEntity<Object> getSlaveLoad() {
        return null;
    }

    @RequestMapping(value = "/twt/monitor/scale", method = RequestMethod.POST)
    public ResponseEntity<Object> scaleUp() {
        return null;
    }

}