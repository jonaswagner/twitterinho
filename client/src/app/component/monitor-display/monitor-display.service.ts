import {Http, Response} from "@angular/http";
import 'rxjs/add/operator/map';
import {Observable} from "rxjs";
import {Injectable} from "@angular/core";
/**
 * Created by flaviokeller on 20.03.17.
 */

@Injectable()
export class MonitorDisplayService{

  constructor(
    private http: Http
  ){

  }
  // getResources(): Observable<any[]> {
  //   return this.http.get("/twt/sentiments").map((response:Response) => response.json());
  // }

}
