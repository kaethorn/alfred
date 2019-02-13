import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Router } from '@angular/router';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';

import { VolumesService } from '../../volumes.service';
import { ComicsService } from '../../comics.service';
import { Volume } from '../../publisher';
import { Comic } from '../../comic';

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
    private router: Router,
    private sanitizer: DomSanitizer,
    private comicsService: ComicsService,
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

  public resumeVolume(publisher: string, series: string, volume: string): void {
    this.comicsService.getLastUnreadByVolume(publisher, series, volume)
      .subscribe((comic: Comic) => {
        this.router.navigate(['/read', comic.id, comic.currentPage]);
      });
  }
}
