import { TestBed } from '@angular/core/testing';
import { Router, ActivatedRouteSnapshot } from '@angular/router';
import { of } from 'rxjs';

import { AuthGuard } from './auth.guard';
import { UserService } from './user.service';

const userService = { user: of({}) };
const router: jasmine.SpyObj<Router> = jasmine.createSpyObj('Router', ['navigate']);

let authGuard: AuthGuard;

describe('AuthGuard', () => {

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        AuthGuard,
        { provide: UserService, useValue: userService },
        { provide: Router, useValue: router }
      ]
    });
  });

  describe('canActivate', () => {

    it('should return true for a logged in user', async () => {
      userService.user = of({});
      authGuard = new AuthGuard((userService as any), (router as any));

      expect(await (authGuard.canActivate(new ActivatedRouteSnapshot(), ({ url: '/settings' } as any)) as Promise<boolean>)).toBe(true);
    });

    it('should navigate to home for a logged out user', async () => {
      userService.user = of('Login error');
      authGuard = new AuthGuard((userService as any), (router as any));

      expect(await (authGuard.canActivate(({} as any), ({ url: '/settings' } as any)) as Promise<boolean>)).toBe(false);
      expect(router.navigate).toHaveBeenCalledWith(['/login'], { queryParams: { target: '/settings' } });
    });
  });
});
