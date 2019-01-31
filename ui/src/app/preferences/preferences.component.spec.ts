import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TestModule } from './../../testing/test.module';
import { PreferencesServiceMocks as preferencesService } from './../../testing/preferences.service.mocks';

import { PreferencesService } from '../preferences.service';
import { PreferencesComponent } from './preferences.component';

describe('PreferencesComponent', () => {
  let component: PreferencesComponent;
  let fixture: ComponentFixture<PreferencesComponent>;

  beforeEach(async(() => {
    const testModule: any = TestModule();
    testModule.providers.push({
      provide: PreferencesService, useValue: preferencesService
    });
    TestBed.configureTestingModule(testModule).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PreferencesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
