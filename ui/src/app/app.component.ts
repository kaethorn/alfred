import { Component } from '@angular/core';

import { Platform } from '@ionic/angular';
import { SplashScreen } from '@ionic-native/splash-screen/ngx';
import { StatusBar } from '@ionic-native/status-bar/ngx';

import { UserService } from './user.service';
import { User } from './user';

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

  user: User;

  constructor (
    private platform: Platform,
    private splashScreen: SplashScreen,
    private statusBar: StatusBar,
    private userService: UserService
  ) {
    this.initializeApp();
    this.userService.user.subscribe((user: User) => {
      this.user = user;
    });
  }

  initializeApp () {
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
