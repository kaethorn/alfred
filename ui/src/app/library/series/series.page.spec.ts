import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { VolumesServiceMocks } from '../../../testing/volumes.service.mocks';
import { VolumesService } from '../../volumes.service';
import { LibraryPageModule } from '../library.module';

import { SeriesPage } from './series.page';

let component: SeriesPage;
let fixture: ComponentFixture<SeriesPage>;
let volumesService: jasmine.SpyObj<VolumesService>;

describe('SeriesPage', () => {

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
    fixture = TestBed.createComponent(SeriesPage);
    component = fixture.componentInstance;
    component.ionViewDidEnter();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('#filter', () => {

    it('filters the series by name', () => {
      expect(component.series.length).toBe(5);
      component.filter('man');
      expect(component.series.length).toBe(2);
      component.filter('Foo');
      expect(component.series.length).toBe(0);
    });
  });
});
