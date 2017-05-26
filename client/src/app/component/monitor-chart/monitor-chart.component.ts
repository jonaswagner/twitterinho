import {Message, UIChart} from "primeng/primeng";
import {Component, Input, OnChanges, SimpleChanges, ViewChild} from "@angular/core";
/**
 * Created by flaviokeller on 29.03.17.
 */

@Component({
    selector: 'monitor-chart',
    templateUrl: './monitor-chart.component.html'
  }
)
export class MonitorChartComponent implements OnChanges {
  @ViewChild('doughnutchart') doughnutChart: UIChart;
  @ViewChild('linechart') lineChart: UIChart;
  @Input()
  monitorData: number;
  @Input()
  monitorMax: number;
  @Input()
  requestCount: number;
  options: any;
  doughnutData: any;
  lineData: any;

  ngOnChanges(changes: SimpleChanges): void {
    this.doughnutData.datasets[0].data = [this.monitorData, this.monitorMax - this.monitorData];
    if (this.lineData.datasets[0].data[0] === undefined) {
      this.lineData.datasets[0].data.pop();
    }
    this.lineData.datasets[0].data.push(this.monitorData);
    setTimeout(() => {
      if (this.lineData.labels.length === this.lineData.datasets[0].data.length - 1) {
        this.lineData.datasets[0].data.shift();
      }
      this.doughnutChart.refresh();
      this.lineChart.refresh();
    }, 100);
  }

  constructor() {
    let bodyStyles = window.getComputedStyle(document.body);
    let twitterblue = bodyStyles.getPropertyValue('--twitterblue').trim();
    let alterorange = bodyStyles.getPropertyValue('--alterorange').trim();
    this.doughnutData = {
      labels: ['Used', 'Free'],
      datasets: [{
        data: [],
        backgroundColor: [alterorange, twitterblue]
      }]
    };
    this.lineData = {
      labels: ['1', '2', '3', '4', '5', '6', '7'],
      datasets: [
        {
          data: [],
          fill: alterorange,
          borderColor: alterorange
        }
      ]
    };
    this.options = {
      legend: {
        display: false
      },
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
}
