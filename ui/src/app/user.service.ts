import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { User } from './user';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  constructor (private http: HttpClient) {}

  private readonly userUrl = 'api/user';
  private readonly logoutUrl = '/logout';

  get (): Observable<User> {
    return this.http.get<User>(this.userUrl);
  }

  logout (): Observable<any> {
    return this.http.post(this.logoutUrl, {}, { responseType: 'text' });
  }
}
