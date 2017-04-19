import {Http, RequestOptions, Headers, Response} from "@angular/http";
import 'rxjs/add/operator/map';
import {Observable, Subject} from "rxjs";
import {Injectable} from "@angular/core";
import {Sentiment} from "../model/sentiment";
/**
 * Created by flaviokeller on 20.03.17.
 */

@Injectable()
export class SentimentService {

  private sentiments: Sentiment[];
  private sentimentSource = new Subject<Sentiment>();
  sentimentStream$ = this.sentimentSource.asObservable();

  constructor(private http: Http) {

  }

  addSentiment(sentiment: string): Observable<Sentiment> {
    let headers = new Headers({'Content-Type': 'application/json'});
    let options = new RequestOptions({headers: headers});

    return this.http.post("/twt/sentiment", {sentiment}, options)
      .map((response: Response) => <Sentiment>response.json()) // ...and calling .json() on the response to return data
      .catch(
        (error: any) => Observable.throw(error.json().error || 'Server error')); //...errors if a;
  }

  // getResources(): Observable<any[]> {
  //   return this.http.get("/api/resources").map(response => <Sentiment[]>response.json());
  // }

  getSentiments(): Observable<Sentiment[]> {

    return this.http.get("/twt/sentiments")
      .map((response: Response) => <Sentiment[]>response.json())
      .catch(
        (error: any) => Observable.throw(error.json().error || 'Server error')
      );
    //return this.sentiments;
  }

  displaySentiment(sentiment: Sentiment) {
    this.sentimentSource.next(sentiment);
  }
}
