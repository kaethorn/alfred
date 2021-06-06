import { Component, NgZone, OnDestroy } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { filter } from 'rxjs/operators';

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
    this.subscription = this.userService.user
      .pipe(filter(user => !!user.id))
      .subscribe(() => {
        this.ngZone.run(() => {
          this.loginInProgress = false;
          if (this.route.snapshot.queryParams.target) {
            this.router.navigate([ this.route.snapshot.queryParams.target ]);
          } else {
            this.router.navigate([ '/library' ]);
          }
        });
      }, (error: string) => {
        this.ngZone.run(() => {
          this.loginInProgress = false;
          this.message = error;
        });
      });
  }

  public ngOnDestroy(): void {
    this.subscription?.unsubscribe();
  }

  public onSubmit(): void {
    this.loginInProgress = true;
    this.userService.login(this.loginForm.get('username')?.value, this.loginForm.get('password')?.value);
  }
}
