import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { VolumesServiceMocks } from '../../../testing/volumes.service.mocks';
import { VolumesService } from '../../volumes.service';
import { LibraryPageModule } from '../library.module';

import { PublishersComponent } from './publishers.component';

let component: PublishersComponent;
let fixture: ComponentFixture<PublishersComponent>;
let volumesService: jasmine.SpyObj<VolumesService>;

describe('PublishersComponent', () => {

  beforeEach(() => {
    volumesService = VolumesServiceMocks.volumesService;

    TestBed.configureTestingModule({
      imports: [
        LibraryPageModule,
        RouterTestingModule
      ],
      providers: [{
        provide: VolumesService, useValue: volumesService
      }]
    });
    fixture = TestBed.createComponent(PublishersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
