/**
 * Created by flaviokeller on 20.03.17.
 */

import {Component, OnDestroy, OnInit} from '@angular/core';
import {Term} from "../../model/term";
import {SentimentService} from "../../service/sentiment.service";
import {Subscription} from "rxjs/Subscription";
@Component({
  selector: 'search-component',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css'],
})

export class SearchComponent implements OnInit, OnDestroy {

  ngOnInit(): void {
    this.getTerms();
  }

  private tweet: any[] = [];
  private activeSentiments: Term[] = [];
  private addedSentiment: Term;
  private addedSentimentString: string;
  private sentimentSubscription: Subscription;

  constructor(private sentimentService: SentimentService) {
  }

  addSentiment() {
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


  getTerms() {

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
    this.sentimentService.startStream(sentiment).subscribe(
      data => {
      },
      err => console.log(err),
      () => console.log("done")
    );
    this.sentimentSubscription = this.sentimentService.getStream(sentiment).subscribe(
      data => {
        let currentSentiment = this.activeSentiments.find(currentSentiment => currentSentiment.name == sentiment.name);
        currentSentiment.values.push(data);
        this.sentimentService.displaySentiment(currentSentiment);
      },
      err => {
        console.log(err);
      },
      () => console.log("done")
    );

  }

  ngOnDestroy(): void {
    this.sentimentSubscription.unsubscribe();
  }
}
