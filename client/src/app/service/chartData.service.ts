import {Injectable} from "@angular/core";
import {Subject} from "rxjs";
/**
 * Created by flaviokeller on 29.03.17.
 */


@Injectable()
export class ChartDataService {

  // Observable string sources
  private missionAnnouncedSource = new Subject<string>();
  private missionConfirmedSource = new Subject<string>();
  // Observable string streams
  missionAnnounced$ = this.missionAnnouncedSource.asObservable();
  missionConfirmed$ = this.missionConfirmedSource.asObservable();
  // Service message commands
  announceMission(mission: string) {
    this.missionAnnouncedSource.next(mission);
  }
}
