import { Component, NgZone } from '@angular/core';
import { Router } from '@angular/router';

import { UserService } from '../user.service';
import { User } from '../user';

@Component({
  selector: 'app-login',
  templateUrl: './login.page.html',
  styleUrls: ['./login.page.sass'],
})
export class LoginPage {

  constructor (
    private userService: UserService,
    private ngZone: NgZone,
    private router: Router,
  ) {
    this.userService.setupGoogleSignIn();
    this.userService.user.subscribe((user: User) => {
      if (user) {
        this.ngZone.run(() => {
          this.router.navigate(['/library']);
        });
      }
    });
  }
}
