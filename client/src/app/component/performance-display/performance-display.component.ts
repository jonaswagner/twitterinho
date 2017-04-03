/**
 * Created by flaviokeller on 20.03.17.
 */

import {Component} from '@angular/core';
import {PerformanceDisplayService} from './performance-display.service';
@Component({
  selector: 'performance-display-component',
  templateUrl: './performance-display.component.html',
  styleUrls: ['./performance-display.component.css'],

})

export class PerformanceDisplayComponent {

  private tweet: any[] = [];

  doughnutData: any;
  lineData: any;

  constructor(private performanceDisplayService: PerformanceDisplayService) {
    this.doughnutData = {
      labels: ['A', 'B', 'C'],
      datasets: [
        {
          data: [300, 50, 100],
          backgroundColor: [
            "#FF6384",
            "#36A2EB",
            "#FFCE56"
          ],
          hoverBackgroundColor: [
            "#FF6384",
            "#36A2EB",
            "#FFCE56"
          ]
        }]
    };
    this.lineData = {
      labels: ['1', '2', '3', '4', '5', '6', '7'],
      datasets: [
        {
          label: 'A',
          data: [35, 47, 96, 87, 64, 71, 32],
          fill: false,
          borderColor: "#FF6384"
        },
        {
          label: 'B',
          data: [4, 78, 62, 12, 36, 84, 55],
          fill: false,
          borderColor: "#36A2EB",
        },
        {
          label: 'C',
          data: [77, 42, 56, 31, 64, 11, 25],
          fill: false,
          borderColor: "#FFCE56"
        }

      ]
    };
  }
}
