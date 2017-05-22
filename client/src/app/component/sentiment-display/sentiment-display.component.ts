/**
 * Created by flaviokeller on 20.03.17.
 */

import {Component, OnDestroy, OnInit} from '@angular/core';
import {SentimentService} from "../../service/sentiment.service";
import {Term} from "../../model/term";
import {Subscription} from "rxjs";

@Component({
  selector: 'sentiment-display-component',
  templateUrl: './sentiment-display.component.html',
  styleUrls: ['./sentiment-display.component.css'],
})

export class SentimentDisplayComponent implements OnInit, OnDestroy {
  ngOnInit(): void {
  }
  selectedTerm: Term = new Term();
  subscription: Subscription;

  constructor(private sentimentService: SentimentService) {
    this.subscription = this.sentimentService.sentimentStream$.subscribe(
      term => {
        this.selectedTerm = term;
      },
      err => {
        console.log(err);
      },
      () => console.log('done'));
  }

  ngOnDestroy() {
    // prevent memory leak when component destroyed
    this.subscription.unsubscribe();
  }

  cancelStream() {
    this.sentimentService.setStopStream(true);
    this.sentimentService.stopStream(this.selectedTerm).subscribe(
      data => {
        console.log("canceled current term: " + data);
      },
      err => console.log(err),
      () => console.log("done")
    )
  }

}
