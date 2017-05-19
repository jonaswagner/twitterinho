/**
 * Created by flaviokeller on 20.03.17.
 */

import {Component, Input, OnChanges, OnDestroy, OnInit, SimpleChanges, ViewChild} from '@angular/core';
import {MonitorDisplayService} from './monitor-display.service';
import {MonitorService} from "../../service/monitor.service";
import {MonitorData} from "../../model/monitorData";
import {Subscription} from "rxjs/Subscription";
import {UIChart} from "primeng/primeng";
@Component({
  selector: 'monitor-display-component',
  templateUrl: './monitor-display.component.html',
  styleUrls: ['./monitor-display.component.css'],

})

export class MonitorDisplayComponent implements OnInit, OnDestroy, OnChanges {
  @ViewChild('chart') chart: UIChart;
  @Input()
  monitorData: MonitorData = new MonitorData();
  private subscription: Subscription;
  cpuDoughnutData: any;
  ramDoughnutData: any;
  cpuLineData: any;
  ramLineData: any;

  ngOnInit(): void {
    this.getMonitorData();
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.cpuDoughnutData.datasets[0].data = this.monitorData.loadAverage;
    this.cpuLineData.datasets[0].data.concat(this.monitorData.loadAverage);
    this.ramDoughnutData.datasets[0].data = this.monitorData.freeSwapSize;
    this.ramLineData.datasets[0].data.concat(this.monitorData.freeSwapSize);
    setTimeout(() => {
      if (this.cpuLineData.labels.length === this.cpuLineData.datasets[0].data.length - 1) {
        this.cpuLineData.datasets[0].data.shift();
      }
      if (this.ramLineData.labels.length === this.ramLineData.datasets[0].data.length - 1) {
        this.ramLineData.datasets[1].data.shift();
      }
      this.chart.refresh();
    }, 100);
  }


  constructor(private monitorService: MonitorService) {
    let bodyStyles = window.getComputedStyle(document.body);
    let twitterblue = bodyStyles.getPropertyValue('--twitterblue').trim();
    let alterorange = bodyStyles.getPropertyValue('--alterorange').trim();
    this.cpuDoughnutData = {
      labels: ['Free', 'Used'],
      datasets: [{
        data: [22,88],
        backgroundColor: [twitterblue, alterorange]
      }]
    };
    this.cpuLineData = {
      labels: ['1', '2', '3', '4', '5', '6', '7'],
      datasets: [
        {
          label: 'A',
          data: [35, 47, 96, 87, 64, 71, 32],
          // fill: false,
          borderColor: alterorange
        }
      ]
    };
    this.ramDoughnutData = {
      labels: ['Free', 'Used'],
      datasets: [{
        data: [22,88],
        backgroundColor: [twitterblue, alterorange]
      }]
    };
    this.ramLineData = {
      labels: ['1', '2', '3', '4', '5', '6', '7'],
      datasets: [
        {
          label: 'A',
          data: [35, 47, 96, 87, 64, 71, 32],
          // fill: false,
          borderColor: alterorange
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

  startMonitorData() {
    this.monitorService.setStopMonitor(false);
    this.getMonitorData();
  }

  stopMonitorData() {
    this.monitorService.setStopMonitor(true);
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }
}
