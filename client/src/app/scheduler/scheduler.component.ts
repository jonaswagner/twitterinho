import {Component, ViewChild, AfterViewInit} from '@angular/core';
import {DayPilot} from "daypilot-pro-angular";
import {DataService, CreateEventParams, MoveEventParams} from "./data.service";

@Component({
  selector: 'scheduler-component',
  template: `
  <div class="body">
    <h1>Scheduler</h1>
    <daypilot-scheduler [config]="config" [events]="events" #scheduler1></daypilot-scheduler>
  </div>
  `,
  styles: [`
  .body {
    padding: 10px;
  }
  `]
})
export class SchedulerComponent implements AfterViewInit {
  title = 'Scheduler';

  @ViewChild("scheduler1")
  scheduler: DayPilot.Angular.Scheduler;

  events: any;

  config: any = {
    timeHeaders : [
      {groupBy: "Month", format: "MMMM yyyy"},
      {groupBy: "Day", format: "d"}
    ],
    days: 31,
    startDate: "2017-03-01",
    scale: "Day",
    onTimeRangeSelected: args => {
      let name = prompt("New event name:", "Event");
      this.scheduler.control.clearSelection();
      if (!name) {
        return;
      }
      let params: CreateEventParams = {
        start: args.start.toString(),
        end: args.end.toString(),
        text: name,
        resource: args.resource
      };
      this.ds.createEvent(params).subscribe(result => {
        this.events.push(result);
        this.scheduler.control.message("Event created");
      } );
    },
    onEventMove: args => {
      let params: MoveEventParams = {
        id: args.e.id(),
        start: args.newStart.toString(),
        end: args.newEnd.toString(),
        resource: args.newResource
      };
      this.ds.moveEvent(params).subscribe(result => {
        this.scheduler.control.message("Event moved");
      });
    }
  };

  constructor(private ds: DataService) {}

  ngAfterViewInit(): void {
    this.ds.getResources().subscribe(result => this.config.resources = result);

    var from = this.scheduler.control.visibleStart();
    var to = this.scheduler.control.visibleEnd();
    this.ds.getEvents(from, to).subscribe(result => this.events = result);
  }



}
