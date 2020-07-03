import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { LoadingController } from '@ionic/angular';

import { LoadingControllerMocks } from '../../../testing/loading.controller.mocks';
import { VolumesServiceMocks } from '../../../testing/volumes.service.mocks';
import { VolumesService } from '../../volumes.service';
import { LibraryPageModule } from '../library.module';

import { PublishersPage } from './publishers.page';

let component: PublishersPage;
let fixture: ComponentFixture<PublishersPage>;
let loadingController: jasmine.SpyObj<LoadingController>;
let loadingElement: jasmine.SpyObj<HTMLIonLoadingElement>;
let volumesService: jasmine.SpyObj<VolumesService>;

describe('PublishersPage', () => {

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

    fixture = TestBed.createComponent(PublishersPage);
    component = fixture.componentInstance;

    await loadingController.create.calls.mostRecent().returnValue;
    await loadingElement.present.calls.mostRecent().returnValue;
    tick();
    await volumesService.listPublishers.calls.mostRecent().returnValue.toPromise();

    fixture.detectChanges();
  }));

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
