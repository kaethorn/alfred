import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { SafeUrl } from '@angular/platform-browser';
import { PopoverController, ToastController } from '@ionic/angular';
import { Observable } from 'rxjs';

import { IssueActionsComponent } from './issue-actions/issue-actions.component';
import { ComicsService } from '../comics.service';
import { VolumesService } from '../volumes.service';
import { ComicDatabaseService } from '../comic-database.service';
import { ThumbnailsService } from '../thumbnails.service';
import { ComicStorageService, StoredState } from '../comic-storage.service';
import { Comic } from '../comic';

@Component({
  selector: 'app-issues',
  templateUrl: './issues.page.html',
  styleUrls: ['./issues.page.sass']
})
export class IssuesPage {

  private publisher: string;
  private series: string;
  private volume: string;
  currentRoute: string;
  thumbnails = new Map<string, Observable<SafeUrl>>();
  comics: Array<Comic> = [];
  stored: StoredState = {};

  constructor (
    private comicDatabaseService: ComicDatabaseService,
    private route: ActivatedRoute,
    private comicsService: ComicsService,
    private volumesService: VolumesService,
    private thumbnailsService: ThumbnailsService,
    private popoverController: PopoverController,
    private toastController: ToastController,
    private comicStorageService: ComicStorageService,
  ) { }

  ionViewDidEnter () {
    this.publisher = this.route.snapshot.params.publisher;
    this.series = this.route.snapshot.params.series;
    this.volume = this.route.snapshot.params.volume;
    this.currentRoute = `/issues/${ this.publisher }/${ this.series }/${ this.volume }`;

    this.list();
  }

  markAsRead (comic: Comic): void {
    this.comicsService.markAsRead(comic).subscribe((resultComic) => {
      this.replaceComic(resultComic);
      this.storeSurrounding(comic.nextId);
    });
  }

  markAsUnread (comic: Comic): void {
    this.comicsService.markAsUnread(comic).subscribe((resultComic) => {
      this.replaceComic(resultComic);
      this.storeSurrounding(comic.previousId);
    });
  }

  markAsReadUntil (comic: Comic): void {
    this.volumesService.markAllAsReadUntil(comic).subscribe(() => {
      this.list();
      this.storeSurrounding(comic.nextId);
    });
  }

  async openMenu (event: any, comic: Comic) {
    const popover = await this.popoverController.create({
      component: IssueActionsComponent,
      componentProps: { comic },
      event,
      translucent: true
    });
    popover.onDidDismiss().then((action: any) => {
      if (action.data && action.data.markAsReadUntil) {
        this.markAsReadUntil(action.data.markAsReadUntil);
      }
    });
    await popover.present();
  }

  private list (): void {
    this.comicsService.listByVolume(this.publisher, this.series, this.volume)
      .subscribe((data: Comic[]) => {
        this.comics = data;
        this.comics.forEach((comic: Comic) => {
          this.thumbnails.set(comic.id, this.thumbnailsService.getFrontCover(comic.id));
          this.updateStoredState(comic.id);
          this.comicStorageService.saveIfStored(comic);
        });
      });
  }

  private async updateStoredState (comicId: string) {
    this.stored[comicId] = await this.comicDatabaseService.isStored(comicId);
  }

  private replaceComic (comic: Comic): void {
    this.comicStorageService.saveIfStored(comic);
    this.comics[this.comics.findIndex(c => c.id === comic.id)] = comic;
  }

  private async showToast (message: string, duration: number = 3000) {
    const toast = await this.toastController.create({
      message,
      duration
    });
    toast.present();
  }

  private storeSurrounding (comicId: string) {
    if (!comicId) {
      return;
    }
    this.comicStorageService.storeSurrounding(comicId).then((storedComicIds) => {
      this.showToast('Volume cached.');
      this.stored = storedComicIds;
    });
  }
}
