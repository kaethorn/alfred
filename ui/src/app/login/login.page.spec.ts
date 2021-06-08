import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router, ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { MockComponent } from '../../testing/mock.component';
import { UserServiceMocks } from '../../testing/user.service.mocks';
import { User } from '../user';
import { UserService } from '../user.service';

import { LoginPageModule } from './login.module';
import { LoginPage } from './login.page';

let component: LoginPage;
let fixture: ComponentFixture<LoginPage>;
let userService: jasmine.SpyObj<UserService>;
let router: jasmine.SpyObj<Router>;
let activatedRoute: any;

describe('LoginPage', () => {

  beforeEach(() => {
    userService = UserServiceMocks.userService;
    router = jasmine.createSpyObj('Router', [ 'navigate' ]);
    activatedRoute = {
      snapshot: {
        queryParams: {
        }
      }
    };

    TestBed.configureTestingModule({
      imports: [
        LoginPageModule,
        RouterTestingModule.withRoutes([
          { component: MockComponent, path: 'library' }
        ])
      ],
      providers: [{
        provide: UserService, useValue: userService
      }, {
        provide: ActivatedRoute, useValue: activatedRoute
      }, {
        provide: Router, useValue: router
      }]
    });
    fixture = TestBed.createComponent(LoginPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
    expect(component.message).toBeUndefined();
  });

  describe('when logging in', () => {

    describe('with a login error', () => {

      beforeEach(() => {
        userService.user.next({ error: 'Invalid user.' } as User);
        fixture = TestBed.createComponent(LoginPage);
        component = fixture.componentInstance;
        fixture.detectChanges();
      });

      it('displays the error message', () => {
        expect(component.message).toEqual('Invalid user.');
      });
    });

    describe('without a target parameter', () => {

      beforeEach(() => {
        delete activatedRoute.snapshot.queryParams.target;
        fixture = TestBed.createComponent(LoginPage);
        component = fixture.componentInstance;
        fixture.detectChanges();
      });

      it('navigates to the library', () => {
        expect(router.navigate).toHaveBeenCalledWith([ '/library' ]);
      });
    });

    describe('with a target parameter', () => {

      beforeEach(() => {
        activatedRoute.snapshot.queryParams.target = '/bookmarks';
        fixture = TestBed.createComponent(LoginPage);
        component = fixture.componentInstance;
        fixture.detectChanges();
      });

      it('navigates to the target', () => {
        expect(router.navigate).toHaveBeenCalledWith([ '/bookmarks' ]);
      });
    });
  });

  describe('on destroy', () => {

    it('unsubscribes', () => {
      const spy = spyOn(component['subscription'], 'unsubscribe' as never);
      component.ngOnDestroy();
      expect(spy).toHaveBeenCalled();
    });

    it('handles missing subscription gracefully', () => {
      component['subscription'] = null;
      component.ngOnDestroy();
      expect(component['subscription']).toBeNull();
    });
  });

  describe('#onSubmit', () => {

    it('flags login progress', () => {
      expect(component.loginInProgress).toBe(false);
      component.onSubmit();
      expect(component.loginInProgress).toBe(true);
    });

    it('triggers the login', () => {
      component.loginForm.patchValue({ password: 'foo', username: 'foo@bar.com' });
      component.onSubmit();
      expect(userService.login).toHaveBeenCalledWith('foo@bar.com', 'foo');
    });
  });
});
