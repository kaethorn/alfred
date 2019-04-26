import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TestModule } from './../testing/test.module';
import { UserServiceMocks as userService } from './../testing/user.service.mocks';

import { UserService } from './user.service';
import { AppComponent } from './app.component';

describe('AppComponent', () => {
  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;

  beforeEach(async(() => {
    const testModule: any = TestModule();
    testModule.providers.push({
      provide: UserService, useValue: userService
    });
    TestBed.configureTestingModule(testModule).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the app', () => {
    expect(component).toBeTruthy();
  });

  it('should render title in a h1 tag', () => {
    const compiled = fixture.debugElement.nativeElement;
    expect(compiled.querySelector('mat-toolbar').textContent).toContain('Alfred');
  });

  it('retries user info on startup', () => {
    expect(component.user.email).toEqual('foo@bar.com');
  });
});
