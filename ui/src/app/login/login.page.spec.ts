import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TestModule } from '../../testing/test.module';
import { UserServiceMocks as userService } from '../../testing/user.service.mocks';

import { UserService } from '../user.service';
import { LoginPage } from './login.page';

describe('LoginPage', () => {
  let component: LoginPage;
  let fixture: ComponentFixture<LoginPage>;

  beforeEach(() => {
    const testModule: any = TestModule();
    testModule.providers.push({
      provide: UserService, useValue: userService
    });
    TestBed.configureTestingModule(testModule);
    fixture = TestBed.createComponent(LoginPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
