import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {HttpModule} from '@angular/http';

import {AppComponent} from './app.component';
import {SchedulerModule} from "./scheduler/scheduler.module";
import {TwitterinhoComponent} from "./twitterinho/twitterinho.component";
import {TwitterService} from "./twitterinho/twitterinho.service";
import {ModalModule} from "ng2-bootstrap";

@NgModule({
  declarations: [
    AppComponent,
    TwitterinhoComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule,
    SchedulerModule,
    ModalModule
  ],
  providers: [TwitterService],
  bootstrap: [AppComponent]
})
export class AppModule {
}
