import { Component, NgZone, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';

import { UserService } from '../user.service';

@Component({
  selector: 'app-login',
  styleUrls: [ './login.page.sass' ],
  templateUrl: './login.page.html'
})
export class LoginPage implements OnInit {

  public message: string | null = null;
  public loginForm = this.formBuilder.group({
    password: [ '', Validators.required ],
    username: [ '', Validators.required ]
  });
  public loginInProgress = false;

  constructor(
    private userService: UserService,
    private ngZone: NgZone,
    private router: Router,
    private route: ActivatedRoute,
    private formBuilder: FormBuilder
  ) {
    this.userService.setupGoogleSignIn();
  }

  public ngOnInit(): void {
    this.userService.user.subscribe(() => {
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

  public onSubmit(): void {
    this.loginInProgress = true;
    this.userService.login(this.loginForm.get('username')?.value, this.loginForm.get('password')?.value);
  }
}
