import {Message, UIChart} from "primeng/primeng";
import {Component, Input, OnChanges, OnDestroy, SimpleChanges, ViewChild} from "@angular/core";
import {Term} from "../../model/term";
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

  @Input()
  currentTerm: Term = new Term();
  @Input()
  data: any;
  options: any;
  msgs: Message[];

  ngOnChanges(changes: SimpleChanges): void {
    if (this.data.datasets[0].label === this.currentTerm.name) {
      this.data.datasets[0].data.concat(this.currentTerm.values);
      this.data.datasets[1].data.concat(this.currentTerm.values);
      setTimeout(() => {
        if (this.data.labels.length === this.data.datasets[0].data.length - 1) {
          this.data.datasets[0].data.shift();
        }
        if (this.data.labels.length === this.data.datasets[0].data.length - 1) {
          this.data.datasets[1].data.shift();
        }
        this.chart.refresh();
      }, 100);
    } else {
      this.data.datasets[0].label = this.currentTerm.name + ' - total avg.';
      this.data.datasets[1].label = this.currentTerm.name + ' - recent avg.';
      this.data.datasets[0].data = this.currentTerm.values;
      this.data.datasets[1].data = this.currentTerm.values;
      setTimeout(() => {
        this.chart.reinit();
      }, 100);
    }
  }


  constructor() {

    let bodyStyles = window.getComputedStyle(document.body);
    let twitterblue = bodyStyles.getPropertyValue('--twitterblue').trim();
    let alterorange = bodyStyles.getPropertyValue('--alterorange').trim();
    this.data = {
      labels: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13', '14', '15', ''],
      datasets: [
        {
          label: 'Total Average',
          data: [],
          // fill: false,
          borderColor: twitterblue,
        },
        {
          label: 'Recent Average',
          data: [],
          // fill: false,
          borderColor: alterorange,
        }],


    };
    this.options = {
      scales: {
        yAxes: [{
          display: true,
          ticks: {
            beginAtZero: true,
            steps: 20,
            stepValue: 0.05,
            max: 1.0
          }
        }]
      }
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
