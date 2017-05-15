/**
 * Created by flaviokeller on 20.03.17.
 */

import {Component, OnDestroy, OnInit} from '@angular/core';
import {Term} from "../../model/term";
import {SentimentService} from "../../service/sentiment.service";
import {Subscription} from "rxjs/Subscription";
import {IntervalObservable} from "rxjs/observable/IntervalObservable";
@Component({
  selector: 'search-component',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css'],
})

export class SearchComponent implements OnInit, OnDestroy {

  ngOnInit(): void {
    this.getTerms();
  }

  private activeTerms: Term[] = [];
  private addedTerm: Term;
  private addedTermName: string;
  private sentimentSubscription: Subscription;

  constructor(private sentimentService: SentimentService) {
  }

  addTerm() {
    this.addedTerm = {id: 1, name: this.addedTermName, values: []};

    this.sentimentService.addTerm(this.addedTermName).subscribe(
      data => {
        this.activeTerms.push(this.addedTerm);
        this.sendToDisplay(this.addedTerm);
      },
      err => {
        console.log(err);
      },
      () => console.log("done"))
    ;
    this.addedTermName = "";
    this.sendToDisplay(this.addedTerm)
  }


  deleteTerm(term: Term) {
    this.sentimentService.deleteTerm(term.name).subscribe(
      data => {
        let index: number = this.activeTerms.indexOf(term);
        if (index !== -1) {

          this.activeTerms.splice(index, 1);
        }
      },
      err => console.log(err),
      () => console.log('done')
    );
  }

  getTerms() {
    this.sentimentService.getTerms().subscribe(
      data => {
        this.activeTerms = data;
      },
      err => {
        console.log(err);
      },
      () => console.log("done"))

  }

  sendToDisplay(term: Term) {
    this.sentimentService.displaySentiment(term);
    this.sentimentService.startStream(term).subscribe(
      data => {
      },
      err => console.log(err),
      () => console.log("done")
    );
    this.sentimentSubscription = this.sentimentService.getStream(term).subscribe(
      data => {
        let currentSentiment = this.activeTerms.find(currentSentiment => currentSentiment.name == term.name);
        currentSentiment.values.push(data);
        this.sentimentService.displaySentiment(currentSentiment);
      },
      err => {
        console.log(err);
      },
      () => console.log("done")
    );

  }

  deleteAllTerms() {
    this.sentimentService.deleteAllTerms().subscribe(
      data => {
        this.activeTerms = [];
      },
      err => console.log(err),
      () => console.log("done")
    )
  }

  ngOnDestroy(): void {
    this.sentimentSubscription.unsubscribe();
  }
}
