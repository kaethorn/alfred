import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { VolumesServiceMocks } from '../../../testing/volumes.service.mocks';
import { VolumesService } from '../../volumes.service';
import { LibraryPageModule } from '../library.module';

import { PublishersPage } from './publishers.page';

let component: PublishersPage;
let fixture: ComponentFixture<PublishersPage>;
let volumesService: jasmine.SpyObj<VolumesService>;

describe('PublishersPage', () => {

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
    fixture = TestBed.createComponent(PublishersPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('#filter', () => {

    it('filters the publishers by series', () => {
      expect(component.publishers.length).toBe(5);
      component.filter('Fath');
      expect(component.publishers.length).toBe(1);
      component.filter('W');
      expect(component.publishers.length).toBe(3);
    });
  });
});
