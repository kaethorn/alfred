import { Component, Output, EventEmitter, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';

import { VolumesService } from '../../volumes.service';
import { ComicsService } from '../../comics.service';
import { Volume } from '../../volume';
import { Comic } from '../../comic';

@Component({ selector: 'app-volume',
  templateUrl: './volumes.component.html',
  styleUrls: ['./volumes.component.sass']
})
export class VolumesComponent implements OnInit {

  private volumesData: Volume[] = [];
  volumes: Volume[] = [];
  publisher: string = '';
  series: string = '';

  @Output() updated = new EventEmitter<boolean>();

  constructor (
    private router: Router,
    private route: ActivatedRoute,
    private sanitizer: DomSanitizer,
    private comicsService: ComicsService,
    private volumesService: VolumesService
  ) {
  }

  ngOnInit() {
    this.publisher = this.route.snapshot.params.publisher;
    this.series = this.route.snapshot.params.series;
    this.list(this.publisher, this.series);
  }

  private list (publisher: string, series: string) {
    this.volumesService.listVolumes(publisher, series)
      .subscribe((data: Volume[]) => {
        this.volumesData = data;
        this.volumes = this.volumesData;
      });
  }

  thumbnail(volume: Volume): SafeUrl {
    return this.sanitizer.bypassSecurityTrustUrl(`data:image/jpeg;base64,${ volume.thumbnail }`);
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

  public resumeVolume(volume: Volume): void {
    if (volume.read) {
      this.comicsService.getFirstByVolume(volume.publisher, volume.series, volume.volume)
        .subscribe((comic: Comic) => {
          this.router.navigate(['/read', comic.id, comic.currentPage]);
        });
    } else {
      this.comicsService.getLastUnreadByVolume(volume.publisher, volume.series, volume.volume)
        .subscribe((comic: Comic) => {
          this.router.navigate(['/read', comic.id, comic.currentPage]);
        });
    }
  }
}
