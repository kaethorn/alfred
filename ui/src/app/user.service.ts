import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { User } from './user';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor (
    private http: HttpClient
  ) { }

  get (): Observable<User> {
    return this.http.get<User>('api/user');
  }

  logout (): Observable<any> {
    return this.http.post('/logout', {}, { responseType: 'text' });
  }
}
