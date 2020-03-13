import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NavParams } from '@ionic/angular';

import { volume1 as volume } from '../../../../testing/volume.fixtures';
import { LibraryPageModule } from '../../library.module';

import { VolumeActionsComponent } from './volume-actions.component';

let component: VolumeActionsComponent;
let fixture: ComponentFixture<VolumeActionsComponent>;
let navParams: NavParams;

describe('VolumeActionsComponent', () => {

  beforeEach(() => {
    navParams = new NavParams({ volume });
    TestBed.configureTestingModule({
      imports: [
        LibraryPageModule
      ],
      providers: [{
        provide: NavParams, useValue: navParams
      }]
    });
    fixture = TestBed.createComponent(VolumeActionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
