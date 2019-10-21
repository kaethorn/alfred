import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SafeUrl } from '@angular/platform-browser';
import { PopoverController } from '@ionic/angular';

import { Observable } from 'rxjs';

import { StoredState } from '../../comic-storage.service';
import { VolumesService } from '../../volumes.service';
import { ComicsService } from '../../comics.service';
import { ThumbnailsService } from '../../thumbnails.service';
import { ComicDatabaseService } from 'src/app/comic-database.service';
import { Volume } from '../../volume';
import { Comic } from '../../comic';
import { VolumeActionsComponent } from './volume-actions/volume-actions.component';

@Component({
  selector: 'app-volumes',
  templateUrl: './volumes.component.html',
  styleUrls: ['./volumes.component.sass']
})
export class VolumesComponent {

  private volumesData: Volume[];
  volumes: Volume[];
  publisher = '';
  series = '';
  thumbnails = new Map<string, Observable<SafeUrl>>();
  stored: StoredState = {};

  constructor (
    private router: Router,
    private route: ActivatedRoute,
    private comicsService: ComicsService,
    private thumbnailsService: ThumbnailsService,
    private volumesService: VolumesService,
    private popoverController: PopoverController,
    private comicDatabaseService: ComicDatabaseService,
  ) { }

  ionViewDidEnter () {
    this.publisher = this.route.snapshot.params.publisher;
    this.series = this.route.snapshot.params.series;
    this.list(this.publisher, this.series);
  }

  private list (publisher: string, series: string) {
    this.volumesService.listVolumes(publisher, series)
      .subscribe((data: Volume[]) => {
        this.volumesData = data;
        this.volumes = this.volumesData;
        this.volumes.forEach((volume: Volume) => {
          this.thumbnails.set(volume.firstComicId, this.thumbnailsService.get(volume.firstComicId));
          this.updateStoredState(volume.firstComicId);
        });
      });
  }

  resumeVolume (volume: Volume): void {
    if (volume.read) {
      this.comicsService.getFirstByVolume(volume.publisher, volume.series, volume.volume)
        .subscribe((comic: Comic) => {
          this.router.navigate(['/read', comic.id], {
            queryParams: { page: comic.currentPage, parent: `/library/publishers/${ comic.publisher }/series/${ comic.series }/volumes` }
          });
        });
    } else {
      this.comicsService.getLastUnreadByVolume(volume.publisher, volume.series, volume.volume)
        .subscribe((comic: Comic) => {
          this.router.navigate(['/read', comic.id], {
            queryParams: { page: comic.currentPage, parent: `/library/publishers/${ comic.publisher }/series/${ comic.series }/volumes` }
          });
        });
    }
  }

  async openMenu (event: any, volume: Volume) {
    const popover = await this.popoverController.create({
      component: VolumeActionsComponent,
      componentProps: { volume },
      event,
      translucent: true
    });
    popover.onWillDismiss().finally(() => {
      this.list(this.publisher, this.series);
    });
    await popover.present();
  }

  filter (value: string) {
    this.volumes = this.volumesData
      .filter(volume => volume.volume.match(value));
  }

  private async updateStoredState (comicId: string) {
    this.stored[comicId] = await this.comicDatabaseService.isStored(comicId);
  }
}
