import { Component } from '@angular/core';

import { Platform } from '@ionic/angular';
import { SplashScreen } from '@ionic-native/splash-screen/ngx';
import { StatusBar } from '@ionic-native/status-bar/ngx';

import { UserService } from './user.service';
import { User } from './user';
import { Router, RouterEvent } from '@angular/router';
import { filter } from 'rxjs/operators';
import { UserSettingsService } from './user-settings.service';

@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html',
  styleUrls: ['./app.component.sass']
})
export class AppComponent {
  public appPages = [
    { title: 'Bookmarks', url: '/bookmarks', icon: 'bookmarks' },
    { title: 'Library', url: '/library/publishers', icon: 'book' },
    { title: 'Settings', url: '/settings', icon: 'settings' }
  ];
  public hideMenu = false;
  public user: User;
  private fullScreenUrls = [ '/read' ];

  constructor(
    private platform: Platform,
    private splashScreen: SplashScreen,
    private statusBar: StatusBar,
    private userService: UserService,
    private router: Router,
    private userSettingsService: UserSettingsService
  ) {
    this.userSettingsService.load();
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

  public logout(): void {
    this.userService.logout();
    window.location.reload();
  }

  private initializeApp(): void {
    this.platform.ready().then(() => {
      this.statusBar.styleDefault();
      this.splashScreen.hide();
    });
  }
}
