import {Http, Response} from "@angular/http";
import 'rxjs/add/operator/map';
import {Observable} from "rxjs";
import {Injectable} from "@angular/core";
/**
 * Created by flaviokeller on 20.03.17.
 */

@Injectable()
export class TwitterService{

  constructor(
    private http: Http
  ){

  }
  getResources(): Observable<any[]> {
    return this.http.get("/api/resources").map((response:Response) => response.json());
  }

}
