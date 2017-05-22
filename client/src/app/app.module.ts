import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {HttpModule} from '@angular/http';

import {AppComponent} from './app.component';
import {SearchComponent} from "./component/search/search.component";
import {SentimentDisplayComponent} from "./component/sentiment-display/sentiment-display.component";
import {MonitorDisplayComponent} from "./component/monitor-display/monitor-display.component";
import {DevDisplayComponent} from "./component/dev-display/dev-display.component";
import {ModalModule} from "ng2-bootstrap";
import {ChartModule, GrowlModule} from "primeng/primeng";
import {SentimentService} from "./service/sentiment.service";
import {MonitorService} from "./service/monitor.service";
import {TwitterinhoChartComponent} from "./component/twitterinho-chart/twitterinho-chart.component";
import {MonitorChartComponent} from "./component/monitor-chart/monitor-chart.component";


@NgModule({
  declarations: [
    AppComponent,
    SearchComponent,
    SentimentDisplayComponent,
    MonitorDisplayComponent,
    DevDisplayComponent,
    TwitterinhoChartComponent,
    MonitorChartComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule,
    ModalModule,
    ChartModule,
    GrowlModule
  ],
  providers: [SentimentService, MonitorService],
  bootstrap: [AppComponent]
})
export class AppModule {
}
