import {Http, RequestOptions, Headers, Response} from "@angular/http";
import 'rxjs/add/operator/map';
import {Observable, Subject} from "rxjs";
import {Injectable} from "@angular/core";
import {Term} from "../model/term";
import {MonitorData} from "../model/monitorData";
/**
 * Created by flaviokeller on 20.03.17.
 */

@Injectable()
export class MonitorService {

  private sentiments: Term[];
  private sentimentSource = new Subject<Term>();
  sentimentStream$ = this.sentimentSource.asObservable();
  private subscription: any;
  private stopMonitor: boolean = false;
  private stopStatistics: boolean = false;

  constructor(private http: Http) {

  }

  getMonitorData(): Observable<MonitorData> {
    return Observable.interval(5000).takeWhile(x => !this.stopMonitor).flatMap(
      () => this.http.get("/twt/monitor")
        .map((response: Response) => <MonitorData>response.json())
        .catch(
          (error: any) => Observable.throw(error.json().error || 'Server error')
        ));
  }

  setStopMonitor(isStopped: boolean) {
    this.stopMonitor = isStopped;
    this.stopStatistics = isStopped;
  }

  scaleUp(): Observable<number> {
    let headers = new Headers({'Content-Type': 'application/json'});
    let options = new RequestOptions({headers: headers});

    return this.http.post("/twt/monitor/scale", options)
      .map((response: Response) => <number>response.json())
      .catch(
        (error: any) => Observable.throw(error.json().error || 'Server error')
      );
  }

}
