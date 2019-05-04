import { of } from 'rxjs';

import { user1 as user } from './user.fixtures';

const userService = jasmine.createSpyObj('UserService', [
  'get',
  'logout'
]);

userService.get.and.returnValue( of(user) );
userService.logout.and.returnValue( of(null) );

export { userService as UserServiceMocks };
