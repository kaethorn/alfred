import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable, NgZone } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

import { User } from './user';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  public user: BehaviorSubject<User> = new BehaviorSubject<User>({} as User);
  private auth2: gapi.auth2.GoogleAuth = {} as gapi.auth2.GoogleAuth;

  constructor(
    private ngZone: NgZone,
    private http: HttpClient
  ) { }

  public verifyCurrentUser(): void {
    const currentUser: User = JSON.parse(localStorage.getItem('user') || '{}');
    if (!currentUser.token) {
      this.user.next({ error: 'You\'ve been logged out.' } as User);
      return;
    }

    this.http.get(`/api/user/verify/${ currentUser.token }`).subscribe(() => {
      this.user.next(currentUser);
    }, () => {
      this.logout();
      this.user.next({ error: 'You\'ve been logged out.' } as User);
    });
  }

  public async setupGoogleSignIn(): Promise<void> {
    if (typeof gapi === 'object') {
      const clientId = await this.getClientId();
      window.gapi.load('auth2', () => {
        this.ngZone.run(() => {
          this.auth2 = window.gapi.auth2.init({
            client_id: clientId
          });
          this.auth2.attachClickHandler('signin-button', {}, (googleUser: gapi.auth2.GoogleUser) => {
            const token = googleUser.getAuthResponse().id_token;
            this.http.post<User>(`/api/user/sign-in/${ token }`, null).subscribe((user: User) => {
              this.user.next(user);
              localStorage.setItem('token', user.token);
              localStorage.setItem('user', JSON.stringify(user));
            }, (response: HttpErrorResponse) => {
              const message = response.error.message ? response.error.message : response.message;
              this.user.next({ error: `Login failure: ${ message }` } as User);
            });
          }, () => {
            this.user.next({ error: 'Login failure: Google-SignIn error.' } as User);
          });

          if (this.auth2.isSignedIn.get() === true) {
            this.auth2.signIn();
          }
        });
      });
    } else {
      const mockUser: User = {
        email: 'b.wayne@waynecorp.com',
        error: null,
        id: 'b.wayne@waynecorp.com',
        name: 'B.Wayne',
        picture: 'https://img.icons8.com/office/80/000000/batman-old.png',
        token: 'mock-123'
      };
      this.http.get(`/api/user/verify/${ mockUser.token }`).subscribe(() => {
        this.user.next(mockUser);
      });
    }
  }

  public login(username: string, password: string): void {
    this.http.post<User>('/api/user/login', { password, username }).subscribe((user: User) => {
      this.user.next(user);
      localStorage.setItem('token', user.token);
      localStorage.setItem('user', JSON.stringify(user));
    }, (response: HttpErrorResponse) => {
      const message = response.error.message ? response.error.message : response.message;
      this.user.next({ error: `Login failure: ${ message }` } as User);
    });
  }

  public logout(): void {
    localStorage.removeItem('user');
    localStorage.removeItem('token');
  }

  private getClientId(): Promise<string> {
    return this.http.get('/api/user/client-id', { responseType: 'text' }).toPromise();
  }
}
