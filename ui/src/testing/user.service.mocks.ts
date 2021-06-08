import { BehaviorSubject, of } from 'rxjs';

import { UserService } from '../app/user.service';

import { UserFixtures } from './user.fixtures';

export class UserServiceMocks {

  public static get userService(): jasmine.SpyObj<UserService> {
    const userService = jasmine.createSpyObj('UserService', {
      login: null,
      logout: of(null),
      setupGoogleSignIn: null
    });
    userService.user = new BehaviorSubject(UserFixtures.user);
    return userService;
  }
}
