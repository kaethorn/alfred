import { Component, Input, Output, EventEmitter } from '@angular/core';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';

import { VolumesService } from '../../volumes.service';
import { Volume } from '../../publisher';

@Component({ selector: 'app-volume',
  templateUrl: './volume.component.html',
  styleUrls: ['./volume.component.sass']
})
export class VolumeComponent {

  @Input() volume: Volume;
  @Input() series: string;
  @Input() publisher: string;
  @Output() updated = new EventEmitter<boolean>();

  constructor (
    private sanitizer: DomSanitizer,
    private volumesService: VolumesService
  ) {
  }

  get thumbnail(): SafeUrl {
    return this.sanitizer.bypassSecurityTrustUrl(`data:image/jpeg;base64,${ this.volume.thumbnail }`);
  }

  public markAsRead(volume: Volume) {
    this.volumesService.markAsRead(volume)
      .subscribe(() => {
        this.updated.emit(true);
      });
  }

  public markAsUnread(volume: Volume) {
    this.volumesService.markAsUnread(volume)
      .subscribe(() => {
        this.updated.emit(true);
      });
  }
}
