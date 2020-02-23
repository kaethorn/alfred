import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { ThumbnailsServiceMocks as thumbnailsService } from '../../../testing/thumbnails.service.mocks';
import { VolumesServiceMocks as volumesService } from '../../../testing/volumes.service.mocks';
import { ThumbnailsService } from '../../thumbnails.service';
import { VolumesService } from '../../volumes.service';
import { LibraryPageModule } from '../library.module';

import { VolumesComponent } from './volumes.component';

describe('VolumesComponent', () => {
  let component: VolumesComponent;
  let fixture: ComponentFixture<VolumesComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        LibraryPageModule,
        RouterTestingModule
      ],
      providers: [{
        provide: VolumesService, useValue: volumesService
      }, {
        provide: ThumbnailsService, useValue: thumbnailsService
      }]
    });
    fixture = TestBed.createComponent(VolumesComponent);
    component = fixture.componentInstance;
    component.ionViewDidEnter();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('renders volume information', () => {
    const volumeElement: HTMLElement = fixture.nativeElement;
    expect(volumeElement.textContent).toContain('1999');
  });
});
