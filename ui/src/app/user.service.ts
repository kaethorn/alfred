import { Injectable, NgZone } from '@angular/core';
import { Observable } from 'rxjs';

import { User } from './user';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private user: Observable<User>;
  private auth2;
  private updateUser: Function;

  constructor (
    private ngZone: NgZone,
  ) {
    (window as any).gapi.load('auth2', () => {
      this.ngZone.run(() => {
        this.auth2 = (window as any).gapi.auth2.init({
          client_id: '401455891931-28afa7q3453j1fsdfnlen5tf46oqeadr.apps.googleusercontent.com'
        });
        this.auth2.attachClickHandler('signin-button', {}, (googleUser) => {
          const user = googleUser.getBasicProfile();
          this.set({
            email:  user.getEmail(),
            name: user.getName(),
            picture: user.getImageUrl()
          });
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

  set (user: User) {
    this.updateUser(user);
  }

  get (): Observable<User> {
    return this.user;
  }

  logout (): Observable<void> {
    return new Observable((observer) => {
      (window as any).gapi.auth2.getAuthInstance().signOut().then(() => {
        this.ngZone.run(() => {
          this.user = null;
          observer.next();
          observer.complete();
        });
      });
    });
  }
}
