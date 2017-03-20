import {DataService} from "./data.service";
import {HttpModule} from "@angular/http";
import {BrowserModule} from "@angular/platform-browser";
import {NgModule} from "@angular/core";
import {SchedulerComponent} from "./scheduler.component";
import {DayPilot} from "daypilot-pro-angular";

@NgModule({
  imports:      [ BrowserModule, HttpModule ],
  declarations: [
    DayPilot.Angular.Scheduler,
    DayPilot.Angular.Modal,
    SchedulerComponent
  ],
  exports:      [ SchedulerComponent ],
  providers:    [ DataService ]
})
export class SchedulerModule { }
