import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TestModule } from '../../testing/test.module';
import { SettingsServiceMocks as settingsService } from '../../testing/settings.service.mocks';
import { StatsServiceMocks as statsService } from '../../testing/stats.service.mocks';

import { SettingsService } from '../settings.service';
import { StatsService } from '../stats.service';

import { SettingsPage } from './settings.page';

describe('SettingsPage', () => {
  let component: SettingsPage;
  let fixture: ComponentFixture<SettingsPage>;

  beforeEach(async(() => {
    const testModule: any = TestModule();
    testModule.providers.push({
      provide: SettingsService, useValue: settingsService
    });
    testModule.providers.push({
      provide: StatsService, useValue: statsService
    });
    TestBed.configureTestingModule(testModule).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SettingsPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
