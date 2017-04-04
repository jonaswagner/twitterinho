import {Message, UIChart} from "primeng/primeng";
import {Component, Input, OnChanges, OnDestroy, SimpleChanges, ViewChild} from "@angular/core";
import {Sentiment} from "../../model/sentiment";
import {Subscription} from "rxjs";
import {SentimentService} from "../../service/sentiment.service";
/**
 * Created by flaviokeller on 29.03.17.
 */

@Component({
    selector: 'twt-chart',
    templateUrl: './twitterinho-chart.component.html'
  }
)
export class TwitterinhoChartComponent implements OnChanges {
  @ViewChild('chart') chart: UIChart;

  ngOnChanges(changes: any): void {
    this.data.datasets[0].label = this.currentSentiment.name;
    this.data.datasets[0].data = this.currentSentiment.values;
    setTimeout(() => {
      this.chart.reinit();
    }, 100);
  }

  @Input()
  currentSentiment: Sentiment = new Sentiment();
  data: any;
  msgs: Message[];

  constructor() {

    var bodyStyles = window.getComputedStyle(document.body);
    var twitterblue = bodyStyles.getPropertyValue('--twitterblue').trim();
    this.data = {
      labels: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13', '14', '15'],
      datasets: [
        {
          label: 'First Dataset',
          data: [],
          fill: false,
          borderColor: twitterblue
        }

      ]
    }
  }


  selectData(event) {
    this.msgs = [];
    this.msgs.push({
      severity: 'info',
      summary: 'Data Selected',
      'detail': this.data.datasets[event.element._datasetIndex].data[event.element._index]
    });
  }
}