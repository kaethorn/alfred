import { HttpClientTestingModule, HttpTestingController, TestRequest } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { first } from 'rxjs/operators';

import { User } from './user';
import { UserService } from './user.service';

let service: UserService;
let httpMock: HttpTestingController;

describe('UserService', () => {

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
        localStorage.setItem('user', JSON.stringify({ email: 'a@b.com', token: 'test-token-1' }));
        service.verifyCurrentUser();
      });

      it('accepts valid users', done => {
        const req = httpMock.expectOne('/api/user/verify/test-token-1');
        expect(req.request.method).toBe('GET');
        req.flush('');

        service.user.pipe(first()).subscribe((user: User | string) => {
          expect((user as User).email).toEqual('a@b.com');
          expect((user as User).token).toEqual('test-token-1');
          done();
        });
      });

      it('rejects invalid users', done => {
        const req = httpMock.expectOne('/api/user/verify/test-token-1');
        expect(req.request.method).toBe('GET');
        req.flush('', { status: 401, statusText: 'Unauthorized' });

        service.user.pipe(first()).subscribe(user => {
          expect(user).toEqual('You\'ve been logged out.');
          done();
        });
      });
    });

    describe('without a user', () => {

      beforeEach(() => {
        service.verifyCurrentUser();
      });

      it('rejects missing users', done => {
        service.verifyCurrentUser();

        service.user.pipe(first()).subscribe(user => {
          expect(user).toEqual('You\'ve been logged out.');
          done();
        });
      });
    });
  });

  describe('#setupGoogleSignIn', () => {

    let auth2: any;
    const expectClientIdRead = (): Promise<void> => {
      const promise = service.setupGoogleSignIn();
      const req = httpMock.expectOne('/api/settings/search/findByKey?key=auth.client.id');
      expect(req.request.method).toBe('GET');
      req.flush({
        _links: {
          self: {
            href: 'foo.bar/1'
          }
        },
        comment: 'Google client ID to use for this server',
        id: '1',
        key: 'auth.client.id',
        name: 'Google client ID',
        value: 'mock-google-id-1'
      });
      return promise;
    };

    beforeEach(done => {
      service.user.pipe(first()).subscribe(user => {
        expect(user).toEqual('You\'ve been logged out.');
        done();
      });
    });

    describe('without the Google API', () => {

      beforeEach(() => {
        delete (window as any).gapi;
      });

      it('attempts to authenticate a mock user', async done => {
        await service.setupGoogleSignIn();

        const req = httpMock.expectOne('/api/user/verify/mock-123');
        expect(req.request.method).toBe('GET');
        req.flush('');

        service.user.pipe(first()).subscribe((user: User | string) => {
          expect((user as User).name).toEqual('B.Wayne');
          done();
        });
      });
    });

    describe('with the Google API', () => {

      let req: TestRequest;

      beforeEach(async () => {
        auth2 = {
          attachClickHandler: jasmine.createSpy().and.callFake((id, options, success) => success({
            getAuthResponse: () => ({
              id_token: 'mock-google-token-1'
            })
          })),
          isSignedIn: {
            get: jasmine.createSpy().and.returnValue(true)
          },
          signIn: jasmine.createSpy()
        };
        (window as any).gapi = {
          auth2: {
            init: (): any => auth2
          },
          load: jasmine.createSpy().and.callFake((api, callback) => callback())
        };
        await expectClientIdRead();

        req = httpMock.expectOne('/api/user/sign-in/mock-google-token-1');
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

        beforeEach(() => {
          req.flush({
            email: 'b@c.com',
            token: 'alfred-token-1'
          });
        });

        it('exchanges the auth token for an app specific JWT', done => {
          service.user.pipe(first()).subscribe((user: User | string) => {
            expect((user as User).email).toEqual('b@c.com');
            expect((user as User).token).toEqual('alfred-token-1');
            done();
          });
        });
      });

      describe('without a valid user', () => {

        beforeEach(() => {
          req.flush({}, {
            status: 403,
            statusText: 'User not allowed.'
          });
        });

        it('reports an error', done => {
          service.user.pipe(first()).subscribe((user: User | string) => {
            expect(user)
              .toEqual('Login failure: Http failure response for /api/user/sign-in/mock-google-token-1: 403 User not allowed.');
            done();
          });
        });
      });

      describe('without a valid token', () => {

        beforeEach(() => {
          req.flush({
            message: 'Unable to verify user.'
          }, {
            status: 401,
            statusText: 'Unauthorized'
          });
        });

        it('reports an error', done => {
          service.user.pipe(first()).subscribe((user: User | string) => {
            expect(user)
              .toEqual('Login failure: Unable to verify user.');
            done();
          });
        });
      });
    });

    describe('with error in the click handler', () => {

      beforeEach(async () => {
        auth2 = {
          attachClickHandler: jasmine.createSpy().and.callFake((id, options, success, error) => error()),
          isSignedIn: {
            get: jasmine.createSpy().and.returnValue(false)
          }
        };
        (window as any).gapi = {
          auth2: {
            init: (): any => auth2
          },
          load: jasmine.createSpy().and.callFake((api, callback) => callback())
        };
        await expectClientIdRead();
      });

      it('reports an error', done => {
        service.user.pipe(first()).subscribe((user: User | string) => {
          expect(user).toEqual('Login failure: Google-SignIn error.');
          done();
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
