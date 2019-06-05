import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { User } from './user';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private user: Observable<User>;

  private updateUser: Function;

  constructor (
  ) {
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
}
