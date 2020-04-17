import { Component, NgZone } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';

import { User } from '../user';
import { UserService } from '../user.service';

@Component({
  selector: 'app-login',
  styleUrls: [ './login.page.sass' ],
  templateUrl: './login.page.html'
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
          this.router.navigate([ this.route.snapshot.queryParams.target ]);
        } else {
          this.router.navigate([ '/library' ]);
        }
      });
    });
  }
}
