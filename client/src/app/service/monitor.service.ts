import {Http, RequestOptions, Headers, Response} from "@angular/http";
import 'rxjs/add/operator/map';
import {Observable, Subject} from "rxjs";
import {Injectable} from "@angular/core";
import {Term} from "../model/term";
/**
 * Created by flaviokeller on 20.03.17.
 */

@Injectable()
export class MonitorService {

  private sentiments: Term[];
  private sentimentSource = new Subject<Term>();
  sentimentStream$ = this.sentimentSource.asObservable();

  constructor(private http: Http) {

  }

  addSentiment(sentiment: string): Observable<Term> {
    let headers = new Headers({'Content-Type': 'application/json'});
    let options = new RequestOptions({headers: headers});

    return this.http.post("/twt/sentiment", {sentiment}, options)
      .map((response: Response) => <Term>response.json()) // ...and calling .json() on the response to return data
      .catch(
        (error: any) => Observable.throw(error.json().error || 'Server error')); //...errors if a;
  }


  getCpuLoad(): Observable<number> {

    return this.http.get("/twt/monitor/cpu")
      .map((response: Response) => <number>response.json())
      .catch(
        (error: any) => Observable.throw(error.json().error || 'Server error')
      );
  }

  getTermStatistics(): Observable<number> {

    return this.http.get("/twt/monitor/statistics")
      .map((response: Response) => <number>response.json())
      .catch(
        (error: any) => Observable.throw(error.json().error || 'Server error')
      );
  }

  getWorkLoad(): Observable<number> {

    return this.http.get("/twt/monitor/work")
      .map((response: Response) => <number>response.json())
      .catch(
        (error: any) => Observable.throw(error.json().error || 'Server error')
      );
  }

  getSlaveLoad(): Observable<number> {

    return this.http.get("/twt/monitor/slave")
      .map((response: Response) => <number>response.json())
      .catch(
        (error: any) => Observable.throw(error.json().error || 'Server error')
      );
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

  displaySentiment(sentiment: Term) {
    this.sentimentSource.next(sentiment);
  }
}
