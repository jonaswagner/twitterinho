import {Http, RequestOptions, Headers, Response, URLSearchParams} from "@angular/http";
import 'rxjs/add/operator/map';
import {Observable, Subject} from "rxjs";
import {Injectable} from "@angular/core";
import {Term} from "../model/term";
import {parseHttpResponse} from "selenium-webdriver/http";
import {subscribeOn} from "rxjs/operator/subscribeOn";
/**
 * Created by flaviokeller on 20.03.17.
 */

@Injectable()
export class SentimentService {

  private sentiments: Term[];
  private sentimentSource = new Subject<Term>();
  sentimentStream$ = this.sentimentSource.asObservable();
  private subscription: any;

  constructor(private http: Http) {

  }

  getTerms(): Observable<Term[]> {

    return this.http.get("/twt/terms")
      .map((response: Response) => <Term[]>response.json())
      .catch(
        (error: any) => Observable.throw(error.json().error || 'Server error')
      );
  }

  addTerm(term: string): Observable<Term> {
    let headers = new Headers({'Content-Type': 'application/json'});
    let options = new RequestOptions({headers: headers});

    return this.http.post("/twt/term", {term}, options)
      .map((response: Response) => <Term>response.json()) // ...and calling .json() on the response to return data
      .catch(
        (error: any) => Observable.throw(error.json().error || 'Server error')); //...errors if a;
  }

  deleteTerm(term: string): any {
    return this.http.delete("/twt/term")
      .map((response: Response) => response.status)
      .catch((error: any) => Observable.throw(error.json().error || "Server error"))
  }

  startStream(term: Term): any {
    let headers = new Headers({'Content-Type': 'application/json'});
    let options = new RequestOptions({headers: headers});
    return this.http.post("/twt/term/" + term.name + "/stream/start", options);
  }

  getStream(term: Term): Observable<number> {
    this.subscription = Observable
      .interval(10000);
    return this.subscription.flatMap(
      () =>
        this.http.get("/twt/term/" + term.name + "/stream")
          .map((response: Response) => <number>response.json())
          .catch((error: any) => Observable.throw(error.json().error || "Server error"))
    );
  }

  cancelStream(term: Term): any {
    this.subscription = Observable.interval(0);
    let headers = new Headers({'Content-Type': 'application/json'});
    let options = new RequestOptions({headers: headers});

    return this.http.put("/twt/term/" + term.name + "/stream", options)
      .map((response: Response) => response.json())
      .catch((error: any) => Observable.throw(error.json().error || "Server error"));
  }


  displaySentiment(sentiment: Term) {
    this.sentimentSource.next(sentiment);
  }
}
