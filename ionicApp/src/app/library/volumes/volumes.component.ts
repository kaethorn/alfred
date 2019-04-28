import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { PopoverController } from '@ionic/angular';

import { VolumesService } from '../../volumes.service';
import { ComicsService } from '../../comics.service';
import { Volume } from '../../volume';
import { Comic } from '../../comic';
import { VolumeActionsComponent } from './volume-actions/volume-actions.component';

@Component({ selector: 'app-volume',
  templateUrl: './volumes.component.html',
  styleUrls: ['./volumes.component.sass']
})
export class VolumesComponent implements OnInit {

  private volumesData: Volume[] = [];
  volumes: Volume[] = [];
  publisher: string = '';
  series: string = '';

  constructor (
    private router: Router,
    private route: ActivatedRoute,
    private sanitizer: DomSanitizer,
    private comicsService: ComicsService,
    private volumesService: VolumesService,
    private popoverController: PopoverController
  ) { }

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

  async openMenu(event: any, volume: Volume) {
    const popover = await this.popoverController.create({
      component: VolumeActionsComponent,
      componentProps: { volume: volume},
      event: event,
      translucent: true
    });
    return await popover.present();
  }
}
