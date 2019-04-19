import { Component } from '@angular/core';

import { UserService } from './user.service';
import { User } from './user';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.sass']
})
export class AppComponent {

  user: User;

  constructor(
    private userService: UserService
  ) {
    this.userService.get().subscribe((user: User) => {
      this.user = user;
    });
  }

  logout () {
    this.userService.logout().subscribe(() => {
      window.location.reload();
    });
  }
}
