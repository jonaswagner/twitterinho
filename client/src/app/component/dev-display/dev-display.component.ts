import {Component, OnDestroy} from "@angular/core";
import {Subscription} from "rxjs/Subscription";
import {Term} from "../../model/term";
import {SentimentService} from "../../service/sentiment.service";
/**
 * Created by flaviokeller on 15.05.17.
 */
@Component({
  selector: 'dev-display-component',
  templateUrl: './dev-display.component.html',
  styleUrls: ['./dev-display.component.css'],

})

export class DevDisplayComponent implements OnDestroy {
  selectedTerm: Term = new Term();
  subscription: Subscription;

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

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
  generateArtificialTweets(){
    this.sentimentService.generateArtificialTweets(this.selectedTerm).subscribe(
      data => {
        console.log("generated Artificial tweets for term: " + this.selectedTerm.name);
      },
      err => console.log(err),
      () => console.log("done")
    )
  }
}
