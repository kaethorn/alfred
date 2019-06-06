import { Injectable, NgZone } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';

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
  }

  private login (googleUser: gapi.auth2.GoogleUser) {
    const token = googleUser.getAuthResponse().id_token;
    this.http.post<User>(`api/verify/${ token }`, null).subscribe((user: User) => {
      this.user.next(user);
      localStorage.setItem('token', user.token);
    });
  }

  get (): BehaviorSubject<User> {
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
