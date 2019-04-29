import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { NavParams } from '@ionic/angular';

import { TestModule } from '../../../../testing/test.module';

import { VolumeActionsComponent } from './volume-actions.component'
import { Volume } from '../../../volume';

describe('VolumeActionsComponent', () => {
  let component: VolumeActionsComponent;
  let fixture: ComponentFixture<VolumeActionsComponent>;
  let navParams: NavParams;
  let volume: Volume;

  beforeEach(async(() => {
    volume = {
      volume: 'Vol. 2019',
      series: 'MockSeries',
      publisher: 'MockPublisher',
      issueCount: 4,
      readCount: 2,
      read: false,
      thumbnail: null
    };
    navParams = new NavParams({ volume: volume });
    const testModule: any = TestModule();
    testModule.providers.push({
      provide: NavParams, useValue: navParams
    });
    TestBed.configureTestingModule(testModule).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VolumeActionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
