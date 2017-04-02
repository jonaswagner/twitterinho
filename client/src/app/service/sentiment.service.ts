import {Http, Response} from "@angular/http";
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

  // getResources(): Observable<any[]> {
  //   return this.http.get("/api/resources").map(response => <Sentiment[]>response.json());
  // }

  getSentiments(): Observable<Sentiment[]> {

    return this.http.get("/twt/sentiments").map((response: Response) => <Sentiment[]>response.json());
    //return this.sentiments;
  }
  displaySentiment(sentiment: Sentiment){
    this.sentimentSource.next(sentiment);
  }
}
