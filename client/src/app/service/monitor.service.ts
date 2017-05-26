import {Http, RequestOptions, Headers, Response} from "@angular/http";
import 'rxjs/add/operator/map';
import {Observable, Subject} from "rxjs";
import {Injectable} from "@angular/core";
import {Term} from "../model/term";
import {MonitorData} from "../model/monitorData";
import {environment} from "../../environments/environment";
/**
 * Created by flaviokeller on 20.03.17.
 */

@Injectable()
export class MonitorService {

  private sentimentSource = new Subject<Term>();
  sentimentStream$ = this.sentimentSource.asObservable();
  private stopMonitor: boolean = false;
  private stopStatistics: boolean = false;

  constructor(private http: Http) {
  }

  getMonitorData(): Observable<MonitorData> {
    return Observable.interval(10000).takeWhile(x => !this.stopMonitor).flatMap(
      () => this.http.get(environment.host + "/twt/monitor")
        .map((response: Response) => <MonitorData>response.json())
        .catch(
          (error: any) => Observable.throw(error.json().error || 'Server error')
        ));
  }

  setStopMonitor(isStopped: boolean) {
    this.stopMonitor = isStopped;
    this.stopStatistics = isStopped;
  }

}
