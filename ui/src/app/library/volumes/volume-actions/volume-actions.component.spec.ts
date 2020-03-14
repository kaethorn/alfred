import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NavParams, PopoverController } from '@ionic/angular';
import { throwError } from 'rxjs';

import { PopoverControllerMocks } from '../../../../testing/popover.controller.mocks';
import { volume1 as volume } from '../../../../testing/volume.fixtures';
import { VolumesServiceMocks } from '../../../../testing/volumes.service.mocks';
import { VolumesService } from '../../../volumes.service';
import { LibraryPageModule } from '../../library.module';

import { VolumeActionsComponent } from './volume-actions.component';

let component: VolumeActionsComponent;
let fixture: ComponentFixture<VolumeActionsComponent>;
let navParams: NavParams;
let popoverController: jasmine.SpyObj<PopoverController>;
let volumesService: jasmine.SpyObj<VolumesService>;

describe('VolumeActionsComponent', () => {

  beforeEach(() => {
    popoverController = PopoverControllerMocks.popoverController;
    volumesService = VolumesServiceMocks.volumesService;

    navParams = new NavParams({ volume });
    TestBed.configureTestingModule({
      imports: [
        LibraryPageModule
      ],
      providers: [{
        provide: NavParams, useValue: navParams
      }, {
        provide: VolumesService, useValue: volumesService
      }, {
        provide: PopoverController, useValue: popoverController
      }]
    });

    fixture = TestBed.createComponent(VolumeActionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('#markAsRead', () => {

    it('marks the volme as read', () => {
      component.markAsRead(volume);
      expect(volumesService.markAsRead).toHaveBeenCalledWith(volume);
    });

    describe('on success', () => {

      it('closes the popover', async () => {
        component.markAsRead(volume);
        await volumesService.markAsRead.calls.mostRecent().returnValue.toPromise();

        expect(popoverController.dismiss).toHaveBeenCalled();
      });
    });

    describe('on error', () => {

      beforeEach(() => {
        volumesService.markAsRead.and.returnValue(throwError(''));
      });

      it('closes the popover', async () => {
        component.markAsRead(volume);
        await new Promise(resolve =>
          volumesService.markAsRead.calls.mostRecent().returnValue.toPromise().catch(resolve));

        expect(popoverController.dismiss).toHaveBeenCalled();
      });
    });
  });

  describe('#markAsUnread', () => {

    it('marks the volme as unread', () => {
      component.markAsUnread(volume);
      expect(volumesService.markAsUnread).toHaveBeenCalledWith(volume);
    });

    describe('on success', () => {

      it('closes the popover', async () => {
        component.markAsUnread(volume);
        await volumesService.markAsUnread.calls.mostRecent().returnValue.toPromise();

        expect(popoverController.dismiss).toHaveBeenCalled();
      });
    });

    describe('on error', () => {

      beforeEach(() => {
        volumesService.markAsUnread.and.returnValue(throwError(''));
      });

      it('closes the popover', async () => {
        component.markAsUnread(volume);
        await new Promise(resolve =>
          volumesService.markAsUnread.calls.mostRecent().returnValue.toPromise().catch(resolve));

        expect(popoverController.dismiss).toHaveBeenCalled();
      });
    });
  });
});
