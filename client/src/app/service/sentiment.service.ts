import {Http, RequestOptions, Headers, Response, URLSearchParams} from "@angular/http";
import 'rxjs/add/operator/map';
import {Observable, Subject} from "rxjs";
import {Injectable} from "@angular/core";
import {Term} from "../model/term";
import { environment } from '../../environments/environment.prod';

/**
 * Created by flaviokeller on 20.03.17.
 */

@Injectable()
export class SentimentService {

  private sentimentSource = new Subject<Term>();
  sentimentStream$ = this.sentimentSource.asObservable();
  private subscription: any;
  private isStreamStopped: boolean = false;

  constructor(private http: Http) {
  }

  getTerms(): Observable<Term[]> {

    return this.http.get(environment.host + "/twt/terms")
      .map((response: Response) => <Term[]>response.json())
      .catch(
        (error: any) => Observable.throw(error.json().error || 'Server error')
      );
  }

  deleteTerm(term: string): any {

    return this.http.delete(environment.host + "/twt/term/" + term)
      .map((response: Response) => response.status)
      .catch((error: any) => Observable.throw(error.json().error || "Server error"))
  }

  deleteAllTerms(): any {
    return this.http.delete(environment.host + "/twt/terms");
  }

  startStream(term: Term): any {
    let headers = new Headers({'Content-Type': 'application/json'});
    let options = new RequestOptions({headers: headers});
    return this.http.post(environment.host + "/twt/term/" + term.name, options);
  }

  getStream(term: Term): Observable<number[]> {
    this.subscription = Observable
      .interval(10000).takeWhile(x => !term.isStopped).flatMap(
        () =>
          this.http.get(environment.host + "/twt/term/" + term.name + "/stream")
            .map((response: Response) => <number[]>response.json())
            .catch((error: any) => Observable.throw(error.json().error || "Server error"))
      );
    return this.subscription;
  }

  stopStream(term: Term): any {
    term.isStopped = true;
    let headers = new Headers({'Content-Type': 'application/json'});
    let options = new RequestOptions({headers: headers});

    return this.http.put(environment.host + "/twt/term/" + term.name + "/stream", options);
  }

  generateArtificialTweets(term: Term): any {
    return this.http.get(environment.host + '/twt/generate/' + term.name);
  }


  displaySentiment(sentiment: Term) {
    let changedTerm: Term = {
      id: sentiment.id,
      name: sentiment.name,
      totalAvg: sentiment.totalAvg,
      recentAvg: sentiment.recentAvg,
      isStopped: sentiment.isStopped
    };
    this.sentimentSource.next(changedTerm);
  }

  setStopStream(isStopped: boolean) {
    this.isStreamStopped = isStopped;
  }

}
