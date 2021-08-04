import { Component, NgZone, OnDestroy } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';

import { UserService } from '../user.service';

@Component({
  selector: 'app-login',
  styleUrls: [ './login.page.sass' ],
  templateUrl: './login.page.html'
})
export class LoginPage implements OnDestroy {

  public message: string | null = null;
  public loginForm = this.formBuilder.group({
    password: [ '', Validators.required ],
    username: [ '', Validators.required ]
  });
  public loginInProgress = false;
  private subscription: Subscription | null = null;

  constructor(
    private userService: UserService,
    private ngZone: NgZone,
    private router: Router,
    private route: ActivatedRoute,
    private formBuilder: FormBuilder
  ) {
    this.userService.setupGoogleSignIn();
    this.subscription = this.userService.user.subscribe(user => {
      this.loginInProgress = false;
      this.ngZone.run(() => {
        this.message = user.error;
        if (!user.error && user.id) {
          if (this.route.snapshot.queryParams.target) {
            this.router.navigate([ this.route.snapshot.queryParams.target ]);
          } else {
            this.router.navigate([ '/library' ]);
          }
        }
      });
    });
  }

  public ngOnDestroy(): void {
    this.subscription?.unsubscribe();
  }

  public onSubmit(): void {
    this.loginInProgress = true;
    this.userService.login(this.loginForm.value.username, this.loginForm.value.password);
  }
}
