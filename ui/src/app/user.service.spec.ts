import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { User } from './user';
import { UserService } from './user.service';

describe('UserService', () => {
  let service: UserService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      imports: [ HttpClientTestingModule ]
    });
    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
    TestBed.resetTestingModule();
  });

  describe('#verifyCurrentUser', () => {

    describe('with a user', () => {

      beforeEach(() => {
        localStorage.setItem('user', JSON.stringify({ token: 'test-token-1', email: 'a@b.com' }));
        service.verifyCurrentUser();
      });

      it('accepts valid users', () => {
        const req = httpMock.expectOne('api/user/verify/test-token-1');
        expect(req.request.method).toBe('GET');
        req.flush('');

        service.user.subscribe((user: User) => {
          expect(user.email).toEqual('a@b.com');
          expect(user.token).toEqual('test-token-1');
        });
      });

      it('rejects invalid users', () => {
        const req = httpMock.expectOne('api/user/verify/test-token-1');
        expect(req.request.method).toBe('GET');
        req.flush('', { status: 401, statusText: 'Unauthorized' });

        service.user.subscribe(user => {
          expect(user).toEqual('You\'ve been logged out.');
        });
      });
    });

    describe('without a user', () => {

      beforeEach(() => {
        service.verifyCurrentUser();
      });

      it('rejects missing users', () => {
        service.verifyCurrentUser();

        service.user.subscribe(user => {
          expect(user).toEqual('You\'ve been logged out.');
        });
      });
    });
  });

  describe('#setupGoogleSignIn', () => {

    describe('without the Google API', () => {

      beforeEach(() => {
        delete (window as any).gapi;
      });

      it('attempts to authenticate a mock user', () => {
        service.setupGoogleSignIn();
        const req = httpMock.expectOne('api/user/verify/mock-123');
        expect(req.request.method).toBe('GET');
        req.flush('');

        service.user.subscribe((user: User) => {
          expect(user.name).toEqual('B.Wayne');
        });
      });
    });

    describe('with the Google API', () => {

      const auth2 = {
        attachClickHandler: jasmine.createSpy().and.callFake((id, options, callback) => callback({
          getAuthResponse: () => ({
            id_token: 'mock-google-token-1'
          })
        })),
        isSignedIn: {
          get: jasmine.createSpy().and.returnValue(true)
        },
        signIn: jasmine.createSpy()
      };
      let req;

      beforeEach(() => {
        (window as any).gapi = {
          load: jasmine.createSpy().and.callFake((api, callback) => callback()),
          auth2: {
            init: (): any => auth2
          }
        };
        service.setupGoogleSignIn();
        req = httpMock.expectOne('api/user/sign-in/mock-google-token-1');
        expect(req.request.method).toBe('POST');
      });

      it('sets up Google Sign-In', () => {
        expect((window as any).gapi.load).toHaveBeenCalled();
        expect(auth2.attachClickHandler)
          .toHaveBeenCalledWith('signin-button', {}, jasmine.any(Function), jasmine.any(Function));
      });

      it('checks if the user is signed in already', () => {
        expect(auth2.isSignedIn.get).toHaveBeenCalled();
      });

      describe('when clicking the sign in button', () => {

        it('exchanges the auth token for an app specific JWT', () => {
          req.flush({
            email: 'b@c.com',
            token: 'alfred-token-1'
          });
          service.user.subscribe((user: User) => {
            expect(user.email).toEqual('b@c.com');
            expect(user.token).toEqual('alfred-token-1');
          });
        });
      });
    });
  });

  describe('#logout', () => {

    beforeEach(() => {
      localStorage.setItem('user', 'foo');
      localStorage.setItem('token', 'bar');
    });

    it('removes authentication details from localStorage', () => {
      expect(localStorage.getItem('user')).not.toBeNull();
      expect(localStorage.getItem('token')).not.toBeNull();

      service.logout();

      expect(localStorage.getItem('user')).toBeNull();
      expect(localStorage.getItem('token')).toBeNull();
    });
  });
});
