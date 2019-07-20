import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TestModule } from '../../../testing/test.module';
import { VolumesServiceMocks as volumesService } from '../../../testing/volumes.service.mocks';
import { ThumbnailsServiceMocks as thumbnailsService } from '../../../testing/thumbnails.service.mocks';

import { VolumesService } from '../../volumes.service';
import { ThumbnailsService } from '../../thumbnails.service';
import { VolumesComponent } from './volumes.component';

describe('VolumesComponent', () => {
  let component: VolumesComponent;
  let fixture: ComponentFixture<VolumesComponent>;

  beforeEach(() => {
    const testModule: any = TestModule();
    testModule.providers.push({
      provide: VolumesService, useValue: volumesService
    }, {
      provide: ThumbnailsService, useValue: thumbnailsService
    });
    TestBed.configureTestingModule(testModule);
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
