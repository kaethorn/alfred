import { Injectable, NgZone } from '@angular/core';
import { Observable } from 'rxjs';

import { User } from './user';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private user: Observable<User>;
  private auth2: gapi.auth2.GoogleAuth;
  private updateUser: Function;

  constructor (
    private ngZone: NgZone,
  ) {
    gapi.load('auth2', () => {
      this.ngZone.run(() => {
        this.auth2 = gapi.auth2.init({
          client_id: '401455891931-28afa7q3453j1fsdfnlen5tf46oqeadr.apps.googleusercontent.com'
        });
        this.auth2.attachClickHandler('signin-button', {}, (googleUser) => {
          this.login(googleUser);
        }, (errorData) => {
          console.log(`Login failure: ${ errorData }`);
        });

        if (this.auth2.isSignedIn.get() === true) {
          this.auth2.signIn();
        }
      });
    });

    this.user = new Observable<User>((observer) => {
      this.updateUser = (user) => {
        observer.next(user);
      };
    });
  }

  private login (googleUser: gapi.auth2.GoogleUser) {
    const token = googleUser.getAuthResponse().id_token;
    const user = googleUser.getBasicProfile();
    this.set({
      email:  user.getEmail(),
      name: user.getName(),
      picture: user.getImageUrl()
    });
  }

  set (user: User) {
    this.updateUser(user);
  }

  get (): Observable<User> {
    return this.user;
  }

  logout (): Observable<void> {
    return new Observable((observer) => {
      gapi.auth2.getAuthInstance().signOut().then(() => {
        this.ngZone.run(() => {
          this.user = null;
          observer.next();
          observer.complete();
        });
      });
    });
  }
}
