/**
 * Created by flaviokeller on 20.03.17.
 */

import {Component, OnInit} from '@angular/core';
import {Sentiment} from "../../model/sentiment";
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
  private activeSentiments: Sentiment[] = [];
  private addedSentiment: Sentiment;
  private addedSentimentString: string;

  constructor(private sentimentService: SentimentService) {
  }

  addSentiment() {
    this.addedSentiment = {id: 1, name: this.addedSentimentString, values: []};
    this.activeSentiments.push(this.addedSentiment);
    this.addedSentimentString = "";
  }

  getSentiments() {

    this.sentimentService.getSentiments().subscribe(
      data => {
        this.activeSentiments = data;
      },
      err => {
        console.log(err);
      },
      () => console.log("done"))

  }
  sendToDisplay(sentiment){
    this.sentimentService.displaySentiment(sentiment);
  }
}
