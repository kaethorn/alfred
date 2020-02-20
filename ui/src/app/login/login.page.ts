import { Component, NgZone } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';

import { UserService } from '../user.service';
import { User } from '../user';

@Component({
  selector: 'app-login',
  templateUrl: './login.page.html',
  styleUrls: ['./login.page.sass']
})
export class LoginPage {

  public message: string;

  constructor(
    private userService: UserService,
    private ngZone: NgZone,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.userService.setupGoogleSignIn();
    this.userService.user.subscribe((user: User | string) => {
      this.ngZone.run(() => {
        this.message = typeof user === 'string' ? user : null;
        if (this.route.snapshot.queryParams.target) {
          this.router.navigate([this.route.snapshot.queryParams.target]);
        } else {
          this.router.navigate(['/library']);
        }
      });
    });
  }
}
