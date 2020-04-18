import { Component } from '@angular/core';
import { SafeUrl } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';
import { PopoverController } from '@ionic/angular';

import { Comic } from '../../comic';
import { ComicDatabaseService } from '../../comic-database.service';
import { StoredState, ComicStorageService } from '../../comic-storage.service';
import { ComicsService } from '../../comics.service';
import { Volume } from '../../volume';
import { VolumesService } from '../../volumes.service';

import { VolumeActionsComponent } from './volume-actions/volume-actions.component';

@Component({
  selector: 'app-volumes',
  styleUrls: [ './volumes.page.sass' ],
  templateUrl: './volumes.page.html'
})
export class VolumesPage {

  public volumes: Volume[];
  public publisher = '';
  public series = '';
  public thumbnails = new Map<string, Promise<SafeUrl>>();
  public stored: StoredState = {};
  private volumesData: Volume[];

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private comicsService: ComicsService,
    private comicStorageService: ComicStorageService,
    private volumesService: VolumesService,
    private popoverController: PopoverController,
    private comicDatabaseService: ComicDatabaseService
  ) { }

  public ionViewDidEnter(): void {
    this.publisher = this.route.snapshot.params.publisher;
    this.series = this.route.snapshot.params.series;
    this.list(this.publisher, this.series);
  }

  public resumeVolume(volume: Volume): void {
    if (volume.read) {
      this.comicsService.getFirstByVolume(volume.publisher, volume.series, volume.name)
        .subscribe((comic: Comic) => {
          this.router.navigate([ '/read', comic.id ], {
            queryParams: { page: comic.currentPage, parent: `/library/publishers/${ comic.publisher }/series/${ comic.series }/volumes` }
          });
        });
    } else {
      this.comicsService.getLastUnreadByVolume(volume.publisher, volume.series, volume.name)
        .subscribe((comic: Comic) => {
          this.router.navigate([ '/read', comic.id ], {
            queryParams: { page: comic.currentPage, parent: `/library/publishers/${ comic.publisher }/series/${ comic.series }/volumes` }
          });
        });
    }
  }

  public async openMenu(event: any, volume: Volume): Promise<void> {
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

  public filter(value: string): void {
    this.volumes = this.volumesData
      .filter(volume => volume.name.match(value));
  }

  private async updateStoredState(comicId: string): Promise<void> {
    this.stored[comicId] = await this.comicDatabaseService.isStored(comicId);
  }

  private list(publisher: string, series: string): void {
    this.volumesService.listVolumes(publisher, series)
      .subscribe((data: Volume[]) => {
        this.volumesData = data;
        this.volumes = this.volumesData;
        this.volumes.forEach((volume: Volume) => {
          this.thumbnails.set(volume.firstComicId, this.comicStorageService.getFrontCoverThumbnail(volume.firstComicId));
          this.updateStoredState(volume.firstComicId);
        });
      });
  }
}
