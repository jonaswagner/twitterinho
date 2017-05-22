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

  private currentTerm: Term;
  private activeTerms: Term[] = [];
  private addedTerm: Term;
  private addedTermName: string;
  private sentimentSubscription: Subscription;
  private status: string;
  private alreadyUsed: string;

  constructor(private sentimentService: SentimentService) {
    this.status = 'loading';
  }

  addTerm() {
    this.addedTerm = {id: 1, name: this.addedTermName, totalAvg: [], recentAvg: [], isStopped: false};
    this.sentimentService.setStopStream(false);
    let existingTerm = this.activeTerms.find(currentSentiment => currentSentiment.name.toLowerCase() === this.addedTermName.toLowerCase())
    if (existingTerm) {
      this.alreadyUsed = 'This term is already in use!';
      return;
    }
    this.alreadyUsed = '';
    this.status = 'loading';
    if (this.currentTerm) {
      this.currentTerm.isStopped = true;
      this.sentimentService.stopStream(this.currentTerm).subscribe(
        data => {
          console.log('stopped current term');

        },
        err => console.log(err),
        () => console.log("done")
      );
    }

    this.sentimentService.startStream(this.addedTerm).subscribe(
      data => {
        this.activeTerms.push(this.addedTerm);
        this.sentimentSubscription = this.getStream(this.addedTerm);
        this.currentTerm = this.addedTerm;
      },
      err => {
        console.log(err);
      },
      () => {
        console.log("done");
        this.addedTermName = "";
        this.status = 'active';
      });
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
      () => {
        console.log("done");
        this.status = 'active';
      });
  }

  sendToDisplay(term: Term) {
    this.sentimentService.displaySentiment(term);
  }

  activateStream(term: Term) {
    this.status = 'loading';
    if (this.currentTerm) {
      this.currentTerm.isStopped = true;
      this.sentimentService.stopStream(this.currentTerm).subscribe(
        data => {
          console.log('stopped current term');

        },
        err => console.log(err),
        () => console.log("done")
      );
    }
    this.sentimentService.startStream(term).subscribe(
      data => {
        term.isStopped = false;
        this.currentTerm = term;

      },
      err => console.log(err),
      () => {
        console.log("done");
        this.status = 'active';
      }
    );
    this.sentimentService.setStopStream(false);
    this.sentimentSubscription = this.getStream(term);
  }

  getStream(term: Term): Subscription {
    return this.sentimentService.getStream(term).subscribe(
      data => {
        let currentSentiment = this.activeTerms.find(currentSentiment => currentSentiment.name == term.name);
        if (currentSentiment.totalAvg) {
          currentSentiment.totalAvg.push(data[0]);
        } else {
          currentSentiment.totalAvg = [data[0]];
        }
        if (currentSentiment.recentAvg) {
          currentSentiment.recentAvg.push(data[1]);
        }
        else {
          currentSentiment.recentAvg = [data[1]];
        }
        this.sentimentService.displaySentiment(currentSentiment);
      },
      err => {
        console.log(err);
      },
      () => {
        console.log("done");
      }
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
