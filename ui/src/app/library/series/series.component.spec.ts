import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { VolumesServiceMocks as volumesService } from '../../../testing/volumes.service.mocks';

import { VolumesService } from '../../volumes.service';
import { SeriesComponent } from './series.component';
import { LibraryPageModule } from '../library.module';

describe('SeriesComponent', () => {
  let component: SeriesComponent;
  let fixture: ComponentFixture<SeriesComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        LibraryPageModule,
        RouterTestingModule
      ],
      providers: [{
        provide: VolumesService, useValue: volumesService
      }]
    });
    fixture = TestBed.createComponent(SeriesComponent);
    component = fixture.componentInstance;
    component.ionViewDidEnter();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
