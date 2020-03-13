import { of } from 'rxjs';

import { user1 as user } from './user.fixtures';

const userService = jasmine.createSpyObj('UserService', [
  'logout',
  'setupGoogleSignIn'
]);

userService.logout.and.returnValue(of(null));
userService.user = of(user);

export { userService as UserServiceMocks };
