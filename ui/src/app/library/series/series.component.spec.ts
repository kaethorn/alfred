import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TestModule } from '../../../testing/test.module';
import { VolumesServiceMocks as volumesService } from '../../../testing/volumes.service.mocks';

import { VolumesService } from '../../volumes.service';
import { SeriesComponent } from './series.component';

describe('SeriesComponent', () => {
  let component: SeriesComponent;
  let fixture: ComponentFixture<SeriesComponent>;

  beforeEach(() => {
    const testModule: any = TestModule();
    testModule.providers.push({
      provide: VolumesService, useValue: volumesService
    });
    TestBed.configureTestingModule(testModule);
    fixture = TestBed.createComponent(SeriesComponent);
    component = fixture.componentInstance;
    component.ionViewDidEnter();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
