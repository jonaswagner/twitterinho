import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {HttpModule} from '@angular/http';

import {AppComponent} from './app.component';
import {SearchComponent} from "./component/search/search.component";
import {SentimentDisplayComponent} from "./component/sentiment-display/sentiment-display.component";
import {MonitorDisplayComponent} from "./component/monitor-display/monitor-display.component";
import {SentimentDisplayService} from "./component/sentiment-display/sentiment-display.service";
import {MonitorDisplayService} from "./component/monitor-display/monitor-display.service";
import {ModalModule} from "ng2-bootstrap";
import {ChartModule, GrowlModule} from "primeng/primeng";
import {SentimentService} from "./service/sentiment.service";
import {MonitorService} from "./service/monitor.service";
import {TwitterinhoChartComponent} from "./component/twitterinho-chart/twitterinho-chart.component";


@NgModule({
  declarations: [
    AppComponent,
    SearchComponent,
    SentimentDisplayComponent,
    MonitorDisplayComponent,
    TwitterinhoChartComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule,
    ModalModule,
    ChartModule,
    GrowlModule
  ],
  providers: [SentimentService, SentimentDisplayService, MonitorDisplayService, MonitorService],
  bootstrap: [AppComponent]
})
export class AppModule {
}
