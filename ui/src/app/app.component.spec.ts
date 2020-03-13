import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { SplashScreen } from '@ionic-native/splash-screen/ngx';
import { StatusBar } from '@ionic-native/status-bar/ngx';
import { Platform } from '@ionic/angular';

import { UserServiceMocks as userService } from '../testing/user.service.mocks';

import { AppComponent } from './app.component';
import { UserService } from './user.service';

let statusBarSpy, splashScreenSpy, platformReadySpy, platformSpy;
let component: AppComponent;
let fixture: ComponentFixture<AppComponent>;

describe('AppComponent', () => {

  beforeEach(() => {
    statusBarSpy = jasmine.createSpyObj('StatusBar', ['styleDefault']);
    splashScreenSpy = jasmine.createSpyObj('SplashScreen', ['hide']);
    platformReadySpy = Promise.resolve();
    platformSpy = jasmine.createSpyObj('Platform', { ready: platformReadySpy });

    TestBed.configureTestingModule({
      declarations: [AppComponent],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      providers: [
        { provide: StatusBar, useValue: statusBarSpy },
        { provide: SplashScreen, useValue: splashScreenSpy },
        { provide: Platform, useValue: platformSpy },
        { provide: UserService, useValue: userService }
      ],
      imports: [ RouterTestingModule.withRoutes([])]
    });
    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the app', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize the app', async () => {
    expect(platformSpy.ready).toHaveBeenCalled();
    await platformReadySpy;
    expect(statusBarSpy.styleDefault).toHaveBeenCalled();
    expect(splashScreenSpy.hide).toHaveBeenCalled();
  });

  it('should have menu labels', () => {
    const app = fixture.nativeElement;
    const menuItems = app.querySelectorAll('ion-label');
    expect(menuItems.length).toEqual(4);
    expect(menuItems[0].textContent).toContain('Foo Barfoo@bar.com');
    expect(menuItems[1].textContent).toContain('Bookmarks');
    expect(menuItems[2].textContent).toContain('Library');
    expect(menuItems[3].textContent).toContain('Settings');
  });

  it('should have URLs', () => {
    const app = fixture.nativeElement;
    const menuItems = app.querySelectorAll('ion-item');
    expect(menuItems.length).toEqual(4);
    expect(menuItems[0].getAttribute('ng-reflect-router-link')).toBe(null);
    expect(menuItems[1].getAttribute('ng-reflect-router-link')).toEqual('/bookmarks');
    expect(menuItems[2].getAttribute('ng-reflect-router-link')).toEqual('/library/publishers');
    expect(menuItems[3].getAttribute('ng-reflect-router-link')).toEqual('/settings');
  });

  it('retrieves user info on startup', () => {
    expect(component.user.email).toEqual('foo@bar.com');
  });
});
