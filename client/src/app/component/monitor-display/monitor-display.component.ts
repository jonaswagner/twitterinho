/**
 * Created by flaviokeller on 20.03.17.
 */

import {Component, OnDestroy, OnInit} from '@angular/core';
import {MonitorDisplayService} from './monitor-display.service';
import {MonitorService} from "../../service/monitor.service";
import {MonitorData} from "../../model/monitorData";
import {Subscription} from "rxjs/Subscription";
@Component({
  selector: 'monitor-display-component',
  templateUrl: './monitor-display.component.html',
  styleUrls: ['./monitor-display.component.css'],

})

export class MonitorDisplayComponent implements OnInit, OnDestroy {
  ngOnInit(): void {
    this.getMonitorData();
  }


  private monitorData: MonitorData = new MonitorData();
  private subscription: Subscription;
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

  getMonitorData() {
    this.subscription = this.monitorService.getMonitorData().subscribe(
      data => {
        this.monitorData = data;
      },
      err => {
        console.log(err)
      },
      () => console.log("done")
    );
  }
  startMonitorData(){
    this.monitorService.setStopMonitor(false);
    this.getMonitorData();
  }
  stopMonitorData(){
    this.monitorService.setStopMonitor(true);
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }
}
