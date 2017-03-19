package ch.uzh.ase.controller;

import java.util.concurrent.atomic.AtomicLong;

import ch.uzh.ase.bean.Tweet;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class TweetController {

    //@ResponseStatus(HttpStatus.OK)
    @RequestMapping("/tweet")
    public String tweet() {
        return "twitterinho";
    }
}
