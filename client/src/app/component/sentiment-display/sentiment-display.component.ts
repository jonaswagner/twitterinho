/**
 * Created by flaviokeller on 20.03.17.
 */

import {Component, OnDestroy, OnInit} from '@angular/core';
import {SentimentService} from "../../service/sentiment.service";
import {Sentiment} from "../../model/sentiment";
import {Subscription} from "rxjs";

@Component({
  selector: 'sentiment-display-component',
  templateUrl: './sentiment-display.component.html',
  styleUrls: ['./sentiment-display.component.css'],


})

export class SentimentDisplayComponent implements OnInit, OnDestroy {
  ngOnInit(): void {
  }

  sentiments: Sentiment[] = [];
  selectedSentiment: Sentiment = new Sentiment();
  subscription: Subscription;

  constructor(private sentimentService: SentimentService) {
    this.subscription = this.sentimentService.sentimentStream$.subscribe(
      sentiment => {
        console.log('new sentiment ' + sentiment.name);
        this.selectedSentiment = sentiment;
      },
      err => {
        console.log(err);
      },
      () => console.log('done'));
  }

  // getSentiments() {
  //   this.sentimentService.getSentiments().subscribe(
  //     data => {
  //     },
  //     err => {
  //       console.log(err);
  //     },
  //     () => console.log("done")
  //   );
  // }
  ngOnDestroy() {
    // prevent memory leak when component destroyed
    this.subscription.unsubscribe();
  }

}
