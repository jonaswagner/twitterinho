
package ch.uzh.ase.controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.transaction.Transactional;

import ch.uzh.ase.domain.Event;
import ch.uzh.ase.domain.Resource;
import ch.uzh.ase.domain.Sentiment;
import ch.uzh.ase.repository.EventRepository;
import ch.uzh.ase.repository.ResourceRepository;
import ch.uzh.ase.repository.SentimentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

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

    @RequestMapping(value = "/twt/sentiments", method = RequestMethod.GET)
    public ResponseEntity<List<Sentiment>> getRegisteredSentiments() {
        return new ResponseEntity<>(createSentiments(), HttpStatus.OK);
    }

//    @PostMapping("/api/events/create")
//    @JsonSerialize(using = LocalDateTimeSerializer.class)
//    @Transactional
//    Event createEvent(@RequestBody EventCreateParams params) {
//
//        Resource r = rr.findOne(params.resource);
//
//        Event e = new Event();
//        e.setStart(params.start);
//        e.setEnd(params.end);
//        e.setText(params.text);
//        e.setResource(r);
//
//        er.save(e);
//
//        return e;
//    }
//
//    @PostMapping("/api/events/move")
//    @JsonSerialize(using = LocalDateTimeSerializer.class)
//    @Transactional
//    Event moveEvent(@RequestBody EventMoveParams params) {
//
//        Event e = er.findOne(params.id);
//        Resource r = rr.findOne(params.resource);
//
//        e.setStart(params.start);
//        e.setEnd(params.end);
//        e.setResource(r);
//
//        er.save(e);
//
//        return e;
//    }
//
//    public static class EventCreateParams {
//        public LocalDateTime start;
//        public LocalDateTime end;
//        public String text;
//        public Long resource;
//    }
//
//    public static class EventMoveParams {
//        public Long id;
//        public LocalDateTime start;
//        public LocalDateTime end;
//        public Long resource;
//    }

    private List<Sentiment> createSentiments() {
        List<Sentiment> sentiments;
        System.out.println("entering create");
        Random rand = new Random();
        List<String> wordlist;
        try {
            wordlist = wordList();
            sentiments = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                Sentiment s = new Sentiment(wordlist.get(rand.nextInt(wordlist.size())));
                List<Integer> dummyValues = new ArrayList<>();
                System.out.println(s.getName());
                for (int j = 0; j < 15; j++) {
                    dummyValues.add(rand.nextInt(100));
                }
                s.setValues(dummyValues);
                sentiments.add(s);
            }
        } catch (IOException e) {
            System.out.println("could not read file properly! Using standard words");
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