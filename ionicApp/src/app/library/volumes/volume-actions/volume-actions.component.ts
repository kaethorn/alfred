import { Component, EventEmitter, Output } from '@angular/core';
import { PopoverController, NavParams } from '@ionic/angular';

import { Volume } from '../../../volume';
import { Comic } from '../../../comic';
import { VolumesService } from '../../../volumes.service';

@Component({
  selector: 'app-volume-actions',
  templateUrl: './volume-actions.component.html',
  styleUrls: ['./volume-actions.component.sass'],
})
export class VolumeActionsComponent {

  comic: Comic;

  @Output() updated = new EventEmitter<boolean>();

  constructor(
    private popoverCtrl: PopoverController,
    private volumesService: VolumesService,
    private navParams: NavParams
  ) {
    this.comic = navParams.get('volume');
  }

  public markAsRead(volume: Volume) {
    this.volumesService.markAsRead(volume)
      .subscribe(() => {
        this.updated.emit(true);
        this.popoverCtrl.dismiss();
      });
  }

  public markAsUnread(volume: Volume) {
    this.volumesService.markAsUnread(volume)
      .subscribe(() => {
        this.updated.emit(true);
        this.popoverCtrl.dismiss();
      });
  }
}
