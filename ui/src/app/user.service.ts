import { Injectable, NgZone } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { BehaviorSubject } from 'rxjs';

import { User } from './user';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private user: BehaviorSubject<User> = new BehaviorSubject<User>(null);
  private auth2: gapi.auth2.GoogleAuth;

  constructor (
    private ngZone: NgZone,
    private http: HttpClient
  ) {
    this.setup();
    this.verifyCurentUser();
  }

  // Check if user is already signed in
  private verifyCurentUser () {
    const currentUser: User = JSON.parse(localStorage.getItem('user') || '{}');
    if (!currentUser.token) {
      return;
    }

    this.http.get<User>(`api/user/verify/${ currentUser.token }`).subscribe((user: User) => {
      this.user.next(currentUser);
    }, () => {
      this.user.next(null);
    });
  }

  // Set up Google Sign-In
  private setup () {
    gapi.load('auth2', () => {
      this.ngZone.run(() => {
        this.auth2 = gapi.auth2.init({
          client_id: '401455891931-28afa7q3453j1fsdfnlen5tf46oqeadr.apps.googleusercontent.com'
        });
        this.auth2.attachClickHandler('signin-button', {}, (googleUser: gapi.auth2.GoogleUser) => {
          const token = googleUser.getAuthResponse().id_token;
          this.http.post<User>(`api/user/sign-in/${ token }`, null).subscribe((user: User) => {
            this.user.next(user);
            localStorage.setItem('token', user.token);
            localStorage.setItem('user', JSON.stringify(user));
          });
        }, (errorData) => {
          console.log(`Login failure: ${ errorData }`);
        });

        if (this.auth2.isSignedIn.get() === true) {
          this.auth2.signIn();
        }
      });
    });
  }

  get (): BehaviorSubject<User> {
    return this.user;
  }

  logout (): Promise<void> {
    return new Promise((resolve) => {
      gapi.auth2.getAuthInstance().signOut().then(() => {
        this.ngZone.run(() => {
          this.user.next(null);
          resolve();
        });
      });
    });
  }
}
