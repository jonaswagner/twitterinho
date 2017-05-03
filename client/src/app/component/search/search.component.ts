/**
 * Created by flaviokeller on 20.03.17.
 */

import {Component, OnInit} from '@angular/core';
import {Term} from "../../model/term";
import {SentimentService} from "../../service/sentiment.service";
@Component({
  selector: 'search-component',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css'],
})

export class SearchComponent implements OnInit {

  ngOnInit(): void {
    this.getSentiments();
  }

  private tweet: any[] = [];
  private activeSentiments: Term[] = [];
  private addedSentiment: Term;
  private addedSentimentString: string;

  constructor(private sentimentService: SentimentService) {
  }

  addSentiment() {
//TODO added tech stack: maven, git, primeng
    this.addedSentiment = {id: 1, name: this.addedSentimentString, values: []};

    this.sentimentService.addTerm(this.addedSentimentString).subscribe(
      data => {
        this.activeSentiments.push(data);
        this.sendToDisplay(data);
      },
      err => {
        console.log(err);
      },
      () => console.log("done"))
    ;
    this.addedSentimentString = "";
  }


  getSentiments() {

    this.sentimentService.getTerms().subscribe(
      data => {
        this.activeSentiments = data;
      },
      err => {
        console.log(err);
      },
      () => console.log("done"))

  }

  sendToDisplay(sentiment) {
    this.sentimentService.displaySentiment(sentiment);
  }
}
