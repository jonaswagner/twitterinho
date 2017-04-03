import {animate, Component, state, style, transition, trigger, ViewChild} from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  animations: [
    trigger('slideInOut', [
      state('in', style({
        transform: 'translate3d(0, 0, 0)'
      })),
      state('out', style({
        transform: 'translate3d(100%, 0, 0)'
      })),
      transition('in => out', animate('400ms ease-in-out')),
      transition('out => in', animate('400ms ease-in-out'))
    ])]
})
export class AppComponent {
  title = 'app works!';

  menuState: string = 'out';
  showShowButton: boolean = true;
  showHideButton: boolean = false;

  toggleMenu() {
    // 1-line if statement that toggles the value:
    this.menuState = this.menuState === 'out' ? 'in' : 'out';
    // this.showShowButton = this.menuState === 'out';
    setTimeout(() => {
      this.showHideButton = this.menuState !== 'out';
    }, 200)
  }
}
