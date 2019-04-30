import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TestModule } from '../../testing/test.module';
import { PreferencesServiceMocks as preferencesService } from '../../testing/preferences.service.mocks';
import { StatsServiceMocks as statsService } from '../../testing/stats.service.mocks';

import { PreferencesService } from '../preferences.service';
import { StatsService } from '../stats.service';

import { PreferencesPage } from './preferences.page';

describe('PreferencesPage', () => {
  let component: PreferencesPage;
  let fixture: ComponentFixture<PreferencesPage>;

  beforeEach(async(() => {
    const testModule: any = TestModule();
    testModule.providers.push({
      provide: PreferencesService, useValue: preferencesService
    });
    testModule.providers.push({
      provide: StatsService, useValue: statsService
    });
    TestBed.configureTestingModule(testModule).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PreferencesPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
