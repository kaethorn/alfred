import { Injectable } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';

import { UserService } from './user.service';
import { User } from './user';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(
    private userService: UserService,
    private router: Router
  ) { }

  public canActivate(next: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
    return new Promise<boolean>(resolve => {
      this.userService.user.subscribe((user: User | string) => {
        if (typeof user === 'string') {
          this.router.navigate(['/login'], { queryParams: { target: state.url } });
          resolve(false);
        } else {
          resolve(true);
        }
      });
    });
  }
}
