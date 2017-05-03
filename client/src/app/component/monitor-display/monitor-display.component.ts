/**
 * Created by flaviokeller on 20.03.17.
 */

import {Component} from '@angular/core';
import {MonitorDisplayService} from './monitor-display.service';
import {MonitorService} from "../../service/monitor.service";
@Component({
  selector: 'monitor-display-component',
  templateUrl: './monitor-display.component.html',
  styleUrls: ['./monitor-display.component.css'],

})

export class MonitorDisplayComponent {

  private tweet: any[] = [];

  doughnutData: any;
  lineData: any;
  barData: any;


  constructor(private monitorService: MonitorService) {
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

  getCpuLoad() {
    this.monitorService.getCpuLoad().subscribe(
      load => {
        this.barData = load;
      },
      err => {
        console.log(err)
      },
      () => console.log("done")
    );
  }
}
