import { Component } from '@angular/core';
import { SafeUrl } from '@angular/platform-browser';
import { PopoverController, ToastController } from '@ionic/angular';

import { BookmarkActionsComponent } from './bookmark-actions/bookmark-actions.component';
import { ComicDatabaseService } from '../comic-database.service';
import { ComicStorageService, StoredState } from '../comic-storage.service';
import { Comic } from '../comic';

@Component({
  selector: 'app-bookmarks',
  templateUrl: './bookmarks.page.html',
  styleUrls: ['./bookmarks.page.sass']
})
export class BookmarksPage {

  comics: Comic[];
  thumbnails = new Map<string, Promise<SafeUrl>>();
  syncing = false;
  stored: StoredState = {};

  constructor (
    private comicDatabaseService: ComicDatabaseService,
    private popoverController: PopoverController,
    private comicStorageService: ComicStorageService,
    private toastController: ToastController,
  ) { }

  ionViewDidEnter () {
    this.comicDatabaseService.ready.toPromise().then(() => this.list());
  }

  private list () {
    this.comicStorageService.getBookmarks().then((comics: Comic[]) => {
      this.comics = comics;
      this.comics.forEach((comic: Comic) => {
        this.thumbnails.set(comic.id, this.comicStorageService.getFrontCoverThumbnail(comic.id));
        this.updateStoredState(comic.id);
      });
    });
 }

  async openMenu (event: any, comic: Comic) {
    const popover = await this.popoverController.create({
      component: BookmarkActionsComponent,
      componentProps: { comic },
      event,
      translucent: true
    });
    await popover.present();
  }

  sync (comic: Comic): void {
    this.syncing = true;
    this.comicStorageService.storeSurrounding(comic.id)
      .then(() => {
        this.updateStoredState(comic.id);
        this.showToast('Volume cached.');
        this.syncing = false;
      }).catch((error) => {
        this.showToast('Error while syncing volume.');
        console.error(error);
        this.syncing = false;
      });
  }

  delete (comic: Comic): void {
    this.comicStorageService.deleteVolume(comic).then(() => {
      this.updateStoredState(comic.id);
    });
  }

  private async updateStoredState (comicId: string) {
    this.stored[comicId] = await this.comicDatabaseService.isStored(comicId);
  }

  private async showToast (message: string, duration: number = 3000) {
    const toast = await this.toastController.create({
      message,
      duration
    });
    toast.present();
  }
}
