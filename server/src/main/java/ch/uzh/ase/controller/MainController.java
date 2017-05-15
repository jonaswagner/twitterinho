
package ch.uzh.ase.controller;

import ch.uzh.ase.Application;
import ch.uzh.ase.TweetRetrieval.StreamRegistry;
import ch.uzh.ase.Util.SystemWorkload;
import ch.uzh.ase.domain.Term;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class MainController {

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

    @RequestMapping(value = "/twt/term/{name}", method = RequestMethod.DELETE)
    public void deleteTerm(@PathVariable String name) {
        //deletes all documents for a certain searchId from DB
        Application.getDatabase().deleteSearchIdEntries(name);
    }

    @RequestMapping(value = "/twt/terms", method = RequestMethod.DELETE)
    public void deleteCollection() {
        //deletes whole DB
        Application.getDatabase().deleteCollection();
    }

    @RequestMapping(value = "/twt/term/{name}", method = RequestMethod.POST)
    public void startStream(@PathVariable String name) {
        StreamRegistry.getInstance().register(name);
    }

    @RequestMapping(value = "/twt/term/{name}/stream", method = RequestMethod.GET)
    public double getStream(@PathVariable String name) {
        double averageSentiment = Application.getDatabase().getAverageSentiment(name);
        return Application.getDatabase().getAverageSentiment(name);
    }

    @RequestMapping(value = "/twt/term/{name}/stream", method = RequestMethod.PUT)
    public void cancelStream(@PathVariable String name) {
        StreamRegistry.getInstance().locateStream(name).stopStream();
    }

    @RequestMapping(value = "/twt/monitor", method = RequestMethod.GET)
    public ResponseEntity<SystemWorkload> getMonitorData() {
        SystemWorkload wl = StreamRegistry.getInstance().getWorkloadObserver().getSystemWorkload(); //FIXME jonas: law of demeter abuse!
        return new ResponseEntity<SystemWorkload>(wl, HttpStatus.OK);
    }

    @RequestMapping(value = "/twt/monitor/statistics", method = RequestMethod.GET)
    public ResponseEntity<Object> getTermStatistics() {
        Map<String, Long> termStats = Application.getDatabase().getTermStatistics();
        return null;
    }
}