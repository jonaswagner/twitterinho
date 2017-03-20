/**
 * Created by flaviokeller on 20.03.17.
 */

import {Component} from '@angular/core';
import {TwitterService} from './twitterinho.service';
@Component({
  selector: 'twitterinho-component',
  templateUrl: './twitterinho.component.html',
  styleUrls: ['./twitterinho.component.css']
})

export class TwitterinhoComponent {

  private title = 'fartagainsttrump.org';
  private tweet: any[] = [];

  constructor(private twitterService: TwitterService) {
  }

  addTweet() {
    this.twitterService.getResources().subscribe(
      data => {
        this.tweet = data;
      },
      err => {
        console.log(err);
      },
      () => console.log("done")
    );
    alert("Hallo");
  }
}
