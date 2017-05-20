/**
 * Created by flaviokeller on 20.03.17.
 */

import {Component, Input, OnChanges, OnDestroy, OnInit, SimpleChanges, ViewChild} from '@angular/core';
import {MonitorService} from "../../service/monitor.service";
import {MonitorData} from "../../model/monitorData";
import {Subscription} from "rxjs/Subscription";
import {UIChart} from "primeng/primeng";
@Component({
  selector: 'monitor-display-component',
  templateUrl: './monitor-display.component.html',
  styleUrls: ['./monitor-display.component.css'],

})

export class MonitorDisplayComponent implements OnInit, OnDestroy {
  monitorData: MonitorData = new MonitorData();
  private monitorSubscription: Subscription;
  private statisticsSubscription: Subscription;
  private requestCount = 0;

  ngOnInit(): void {
    this.getMonitorData();
  }

  constructor(private monitorService: MonitorService) {
  }

  getMonitorData() {
    this.monitorSubscription = this.monitorService.getMonitorData().subscribe(
      data => {
        this.requestCount += 1;
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
    this.monitorSubscription.unsubscribe();
    this.statisticsSubscription.unsubscribe();
  }
}
