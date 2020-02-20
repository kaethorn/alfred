import { Router, ActivatedRouteSnapshot } from '@angular/router';
import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

import { UserService } from './user.service';

import { AuthGuard } from './auth.guard';

describe('AuthGuard', () => {

  let authGuard: AuthGuard;
  const userService = { user: of({}) };
  const router = jasmine.createSpyObj('MockRouter', ['navigate']);

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
