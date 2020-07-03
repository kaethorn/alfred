import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { LoadingController } from '@ionic/angular';

import { LoadingControllerMocks } from '../../../testing/loading.controller.mocks';
import { VolumesServiceMocks } from '../../../testing/volumes.service.mocks';
import { VolumesService } from '../../volumes.service';
import { LibraryPageModule } from '../library.module';

import { SeriesPage } from './series.page';

let component: SeriesPage;
let fixture: ComponentFixture<SeriesPage>;
let loadingController: jasmine.SpyObj<LoadingController>;
let loadingElement: jasmine.SpyObj<HTMLIonLoadingElement>;
let volumesService: jasmine.SpyObj<VolumesService>;

describe('SeriesPage', () => {

  beforeEach(<any>fakeAsync(async () => {
    loadingController = LoadingControllerMocks.loadingController;
    loadingElement = LoadingControllerMocks.loadingElementSpy;
    volumesService = VolumesServiceMocks.volumesService;

    TestBed.configureTestingModule({
      imports: [
        LibraryPageModule,
        RouterTestingModule
      ],
      providers: [
        { provide: VolumesService, useValue: volumesService },
        { provide: LoadingController, useValue: loadingController }
      ]
    });
    fixture = TestBed.createComponent(SeriesPage);
    component = fixture.componentInstance;
    component.ionViewDidEnter();

    await loadingController.create.calls.mostRecent().returnValue;
    await loadingElement.present.calls.mostRecent().returnValue;
    tick();
    await volumesService.listSeries.calls.mostRecent().returnValue.toPromise();

    fixture.detectChanges();
  }));

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
