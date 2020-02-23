import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { ComicsServiceMocks as comicsService } from '../../testing/comics.service.mocks';
import { SettingsServiceMocks as settingsService } from '../../testing/settings.service.mocks';
import { StatsServiceMocks as statsService } from '../../testing/stats.service.mocks';
import { ComicsService } from '../comics.service';
import { SettingsService } from '../settings.service';
import { StatsService } from '../stats.service';

import { SettingsPageModule } from './settings.module';
import { SettingsPage } from './settings.page';

describe('SettingsPage', () => {
  let component: SettingsPage;
  let fixture: ComponentFixture<SettingsPage>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        SettingsPageModule,
        RouterTestingModule
      ],
      providers: [{
        provide: SettingsService, useValue: settingsService
      }, {
        provide: ComicsService, useValue: comicsService
      }, {
        provide: StatsService, useValue: statsService
      }]
    });
    fixture = TestBed.createComponent(SettingsPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
