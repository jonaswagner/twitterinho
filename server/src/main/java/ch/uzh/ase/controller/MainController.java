
package ch.uzh.ase.controller;

import ch.uzh.ase.Application;
import ch.uzh.ase.TweetRetrieval.StreamRegistry;
import ch.uzh.ase.domain.Sentiment;
import ch.uzh.ase.repository.SentimentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
public class MainController {

//    @Autowired
//    EventRepository er;

//    @Autowired
//    ResourceRepository rr;

    @Autowired
    SentimentRepository sr;

    @RequestMapping("/api")
    @ResponseBody
    String home() {
        return "Welcome!";
    }

//    @RequestMapping("/api/resources")
//    Iterable<Resource> resources() {
//        return rr.findAll();
//    }

//    @GetMapping("/api/events")
//    @JsonSerialize(using = LocalDateTimeSerializer.class)
//    Iterable<Event> events(@RequestParam("from") @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime from, @RequestParam("to") @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime to) {
//        return er.findBetween(from, to);
//    }

    @RequestMapping(value = "/twt/terms", method = RequestMethod.GET)
    public ResponseEntity<List<Sentiment>> getRegisteredTerms() {

        //String = searchId, Double = Sentimentvalue
        Map<String, Double> allRegistredTerms = Application.getDatabase().getAllAverageSentiments();

        return new ResponseEntity<>(createSentiments(), HttpStatus.OK);
    }


    @RequestMapping(value = "/twt/term", method = RequestMethod.POST)
    public ResponseEntity<Sentiment> addTerm(@RequestBody String searchId) {

        //starts TweetStream for given searchWord
        StreamRegistry.getInstance().register(searchId);

        Sentiment s = new Sentiment(null);

        return new ResponseEntity<Sentiment>(s, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/twt/term", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteTerm(String searchId) {
        //deletes all documents for a certain searchId from DB
        Application.getDatabase().deleteSearchIdEntries(searchId);
        return null;
    }

    //TODO Flavio: neue Methode
    @RequestMapping(value = "/twt/term", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteCollection() {
        //deletes whole DB
        Application.getDatabase().deleteCollection();
        return null;
    }

    @RequestMapping(value = "/twt/term/stream", method = RequestMethod.GET)
    public ResponseEntity<Object> getStream(String searchId) {
        //holt den aktuellen durchschnittlichen Sentimentwert für eine searchId
        double averageSentiment = Application.getDatabase().getAverageSentiment(searchId);
        return null;
    }

    @RequestMapping(value = "/twt/term/stream", method = RequestMethod.PUT)
    public ResponseEntity<Object> cancelStream(String searchId) {
        StreamRegistry.getInstance().locateStream(searchId).stopStream();
        return null;
    }

    @RequestMapping(value = "/twt/monitor/cpu", method = RequestMethod.GET)
    public ResponseEntity<Object> getCpuLoad() {
        return null;
    }

    @RequestMapping(value = "/twt/monitor/statistics", method = RequestMethod.GET)
    public ResponseEntity<Object> getTermStatistics() {
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
    public ResponseEntity<Object> scaleUp(){
        return null;
    }

    //TODO below here needs to be deleted
    private List<Double> createDummyValues() {
        List<Double> dummyValues = new ArrayList<>();
        Random rand = new Random();
        for (int j = 0; j < 15; j++) {
            dummyValues.add((double) Math.round(rand.nextDouble() * 100d) / 100d);
        }
        return dummyValues;
    }

    private List<Sentiment> createSentiments() {
        List<Sentiment> sentiments;
        System.out.println("entering create");
        List<String> wordlist;
        Random rand = new Random();

        try {
            wordlist = wordList();
            sentiments = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                Sentiment s = new Sentiment(wordlist.get(rand.nextInt(wordlist.size())));
                System.out.println(s.getName());
                s.setValues(createDummyValues());
                sentiments.add(s);
            }
        } catch (IOException e) {
            System.out.println("could not read file properly! Using standard words");
            e.printStackTrace();
            sentiments = createStandardList();
        }
        return sentiments;
    }

    private List<Sentiment> createStandardList() {
        List<Sentiment> sentiments = new ArrayList<>();
        sentiments.add(new Sentiment("tree"));
        sentiments.add(new Sentiment("blankness"));
        sentiments.add(new Sentiment("delight"));
        sentiments.add(new Sentiment("brigade"));
        sentiments.add(new Sentiment("recipient"));
        sentiments.add(new Sentiment("universe"));
        sentiments.add(new Sentiment("chalk"));
        sentiments.add(new Sentiment("mechanics"));
        sentiments.add(new Sentiment("management"));
        sentiments.add(new Sentiment("graffito"));
        sentiments.add(new Sentiment("aircraft"));
        sentiments.add(new Sentiment("investigating"));
        sentiments.add(new Sentiment("color"));
        sentiments.add(new Sentiment("librarian"));
        sentiments.add(new Sentiment("microcomputer"));
        sentiments.add(new Sentiment("literate"));
        sentiments.add(new Sentiment("deterrent"));
        sentiments.add(new Sentiment("painting"));
        sentiments.add(new Sentiment("poem"));
        sentiments.add(new Sentiment("damp"));
        return sentiments;
    }

    private List<String> wordList() throws IOException {
        FileReader fileReader = new FileReader("wordlist.txt");
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        List<String> lines = new ArrayList<String>();
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            lines.add(line);
        }
        bufferedReader.close();
        return lines;
    }
}