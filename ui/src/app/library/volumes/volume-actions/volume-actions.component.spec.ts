import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NavParams } from '@ionic/angular';

import { TestModule } from '../../../../testing/test.module';
import { volume1 as volume } from '../../../../testing/volume.fixtures';

import { VolumeActionsComponent } from './volume-actions.component';

describe('VolumeActionsComponent', () => {
  let component: VolumeActionsComponent;
  let fixture: ComponentFixture<VolumeActionsComponent>;
  let navParams: NavParams;

  beforeEach(() => {
    navParams = new NavParams({ volume });
    const testModule: any = TestModule();
    testModule.providers.push({
      provide: NavParams, useValue: navParams
    });
    TestBed.configureTestingModule(testModule);
    fixture = TestBed.createComponent(VolumeActionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
