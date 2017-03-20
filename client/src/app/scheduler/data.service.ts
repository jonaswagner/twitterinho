import { Http, Response } from '@angular/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Rx';
import 'rxjs/Rx';
import {DayPilot} from 'daypilot-pro-angular';

@Injectable()
export class DataService {

  constructor(private http : Http){
  }

  getEvents(from: DayPilot.Date, to: DayPilot.Date): Observable<any[]> {
    return this.http.get("/api/events?from=" + from.toString() + "&to=" + to.toString()).map((response:Response) => response.json());
  }

  getResources(): Observable<any[]> {
    return this.http.get("/api/resources").map((response:Response) => response.json());
  }

  createEvent(data: CreateEventParams): Observable<EventData> {
    return this.http.post("/api/events/create", data).map((response:Response) => response.json());
  }

  moveEvent(data: any): Observable<EventData> {
    return this.http.post("/api/events/move", data).map((response:Response) => response.json());
  }

}

export interface CreateEventParams {
  start: string;
  end: string;
  text: string;
  resource: string | number;
}

export interface MoveEventParams {
  id: string | number;
  start: string;
  end: string;
  resource: string | number;
}

export interface EventData {
  id: string | number;
  start: string;
  end: string;
  text: string;
  resource: string | number;
}

