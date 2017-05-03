import {Http, Response} from "@angular/http";
import 'rxjs/add/operator/map';
import {Observable, Subject} from "rxjs";
import {Injectable} from "@angular/core";
import {Term} from "../../model/term";
/**
 * Created by flaviokeller on 20.03.17.
 */

@Injectable()
export class SearchService{


  constructor(
    private http: Http
  ){

  }
  getResources(): Observable<any[]> {
    return this.http.get("/api/resources").map((response:Response) => response.json());
  }



}
