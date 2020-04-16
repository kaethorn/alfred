import { Component, Inject } from '@angular/core';
import { Router, RouterEvent } from '@angular/router';
import { SplashScreen } from '@ionic-native/splash-screen/ngx';
import { StatusBar } from '@ionic-native/status-bar/ngx';
import { Platform } from '@ionic/angular';
import { filter } from 'rxjs/operators';

import { LOCATION_TOKEN } from './location.token';
import { User } from './user';
import { UserSettingsService } from './user-settings.service';
import { UserService } from './user.service';

@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html',
  styleUrls: [ './app.component.sass' ]
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
    private userSettingsService: UserSettingsService,
    @Inject(LOCATION_TOKEN) private location: Location
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
    this.location.reload();
  }

  private initializeApp(): void {
    this.platform.ready().then(() => {
      this.statusBar.styleDefault();
      this.splashScreen.hide();
    });
  }
}
