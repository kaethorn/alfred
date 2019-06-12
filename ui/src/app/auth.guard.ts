import { Injectable } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';

import { UserService } from './user.service';
import { User } from './user';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor (
    private userService: UserService,
    private router: Router
  ) { }

  canActivate (next: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
    return new Promise<boolean>((resolve, reject) => {
      this.userService.user.subscribe((user: User) => {
        if (user) {
          resolve(true);
        } else {
          this.router.navigate(['/login'], { queryParams: { target: state.url } });
          resolve(false);
        }
      });
    });
  }
}
