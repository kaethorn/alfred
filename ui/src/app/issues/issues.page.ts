import { Component } from '@angular/core';
import { SafeUrl } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { PopoverController, ToastController } from '@ionic/angular';
import { Observable } from 'rxjs';

import { Comic } from '../comic';
import { ComicDatabaseService } from '../comic-database.service';
import { ComicStorageService, StoredState } from '../comic-storage.service';
import { ComicsService } from '../comics.service';
import { ThumbnailsService } from '../thumbnails.service';
import { VolumesService } from '../volumes.service';

import { IssueActionsComponent } from './issue-actions/issue-actions.component';

@Component({
  selector: 'app-issues',
  templateUrl: './issues.page.html',
  styleUrls: [ './issues.page.sass' ]
})
export class IssuesPage {

  public currentRoute: string;
  public thumbnails = new Map<string, Observable<SafeUrl>>();
  public comics: Array<Comic> = [];
  public stored: StoredState = {};
  private publisher: string;
  private series: string;
  private volume: string;

  constructor(
    private comicDatabaseService: ComicDatabaseService,
    private route: ActivatedRoute,
    private comicsService: ComicsService,
    private volumesService: VolumesService,
    private thumbnailsService: ThumbnailsService,
    private popoverController: PopoverController,
    private toastController: ToastController,
    private comicStorageService: ComicStorageService
  ) { }

  public ionViewDidEnter(): void {
    this.publisher = this.route.snapshot.params.publisher;
    this.series = this.route.snapshot.params.series;
    this.volume = this.route.snapshot.params.volume;
    this.currentRoute = `/issues/${ this.publisher }/${ this.series }/${ this.volume }`;

    this.list();
  }

  public markAsRead(comic: Comic): void {
    this.comicsService.markAsRead(comic).subscribe(resultComic => {
      this.replaceComic(resultComic);
      this.storeSurrounding(comic.nextId);
    });
  }

  public markAsUnread(comic: Comic): void {
    this.comicsService.markAsUnread(comic).subscribe(resultComic => {
      this.replaceComic(resultComic);
      this.storeSurrounding(comic.previousId);
    });
  }

  public markAsReadUntil(comic: Comic): void {
    this.volumesService.markAllAsReadUntil(comic).subscribe(() => {
      this.list();
      this.storeSurrounding(comic.nextId);
    });
  }

  public async openMenu(event: Event, comic: Comic): Promise<void> {
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

  private list(): void {
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

  private async updateStoredState(comicId: string): Promise<void> {
    this.stored[comicId] = await this.comicDatabaseService.isStored(comicId);
  }

  private replaceComic(comic: Comic): void {
    this.comicStorageService.saveIfStored(comic);
    this.comics[this.comics.findIndex(c => c.id === comic.id)] = comic;
  }

  private async showToast(message: string): Promise<void> {
    const toast = await this.toastController.create({
      message,
      duration: 3000
    });
    toast.present();
  }

  private storeSurrounding(comicId: string): void {
    if (!comicId) {
      return;
    }
    this.comicStorageService.storeSurrounding(comicId).then(storedComicIds => {
      this.showToast('Volume cached.');
      this.stored = storedComicIds;
    });
  }
}
