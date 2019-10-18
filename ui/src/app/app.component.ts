import { Component } from '@angular/core';

import { Platform } from '@ionic/angular';
import { SplashScreen } from '@ionic-native/splash-screen/ngx';
import { StatusBar } from '@ionic-native/status-bar/ngx';

import { UserService } from './user.service';
import { User } from './user';
import { Router, RouterEvent } from '@angular/router';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html',
  styleUrls: ['./app.component.sass']
})
export class AppComponent {
  appPages = [
    { title: 'Bookmarks', url: '/bookmarks', icon: 'bookmarks' },
    { title: 'Library', url: '/library/publishers', icon: 'book' },
    { title: 'Settings', url: '/settings', icon: 'settings' },
  ];
  hideMenu = false;
  private fullScreenUrls = [ '/read' ];

  user: User;

  constructor (
    private platform: Platform,
    private splashScreen: SplashScreen,
    private statusBar: StatusBar,
    private userService: UserService,
    private router: Router,
  ) {
    this.initializeApp();
    this.userService.user.subscribe((user: User) => {
      this.user = user;
    });
    this.router.events.pipe(
      filter(event => event instanceof RouterEvent)
    ).subscribe((event: RouterEvent) => {
      this.hideMenu = this.fullScreenUrls
        .reduce((result, fullScreenUrl) => result || event.url.startsWith(fullScreenUrl), false);
    });
  }

  private initializeApp () {
    this.platform.ready().then(() => {
      this.statusBar.styleDefault();
      this.splashScreen.hide();
    });
  }

  logout () {
    this.userService.logout();
    window.location.reload();
  }
}
