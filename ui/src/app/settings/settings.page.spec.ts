import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { ComicsServiceMocks } from '../../testing/comics.service.mocks';
import { SettingsServiceMocks } from '../../testing/settings.service.mocks';
import { StatsServiceMocks } from '../../testing/stats.service.mocks';
import { ComicsService } from '../comics.service';
import { SettingsService } from '../settings.service';
import { StatsService } from '../stats.service';

import { SettingsPageModule } from './settings.module';
import { SettingsPage } from './settings.page';

let component: SettingsPage;
let fixture: ComponentFixture<SettingsPage>;
let comicsService: jasmine.SpyObj<ComicsService>;
let settingsService: jasmine.SpyObj<SettingsService>;
let statsService: jasmine.SpyObj<StatsService>;

describe('SettingsPage', () => {

  beforeEach(() => {
    comicsService = ComicsServiceMocks.comicsService;
    settingsService = SettingsServiceMocks.settingsService;
    statsService = StatsServiceMocks.statsService;

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
