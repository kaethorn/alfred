import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { MockComponent } from '../..//testing/mock.component';
import { UserServiceMocks } from '../../testing/user.service.mocks';
import { UserService } from '../user.service';

import { LoginPageModule } from './login.module';
import { LoginPage } from './login.page';

let component: LoginPage;
let fixture: ComponentFixture<LoginPage>;
let userService: jasmine.SpyObj<UserService>;

describe('LoginPage', () => {

  beforeEach(() => {
    userService = UserServiceMocks.userService;

    TestBed.configureTestingModule({
      imports: [
        LoginPageModule,
        RouterTestingModule.withRoutes([
          { path: 'library', component: MockComponent }
        ])
      ],
      providers: [{
        provide: UserService, useValue: userService
      }]
    });
    fixture = TestBed.createComponent(LoginPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
