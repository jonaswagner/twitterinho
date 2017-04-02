/**
 * Created by flaviokeller on 20.03.17.
 */

import {Component} from '@angular/core';
import {PerformanceDisplayService} from './performance-display.service';
@Component({
  selector: 'performance-display-component',
  templateUrl: './performance-display.component.html',
  styleUrls: ['./performance-display.component.css']
})

export class PerformanceDisplayComponent {

  private tweet: any[] = [];

  constructor(private performanceDisplayService: PerformanceDisplayService) {
  }

  addTweet() {
    // this.performanceDisplayService.getResources().subscribe(
    //   data => {
    //     this.tweet = data;
    //   },
    //   err => {
    //     console.log(err);
    //   },
    //   () => console.log("done")
    // );
    // alert("Hallo");
  }
}
