import { Component } from '@angular/core';
import { ToastController } from '@ionic/angular';

import { Observable } from 'rxjs';

import { Comic } from '../../comic';
import { ComicsService } from '../../comics.service';
import { ThumbnailsService } from 'src/app/thumbnails.service';
import { Thumbnail } from 'src/app/thumbnail';

@Component({
  selector: 'app-covers',
  templateUrl: './covers.component.html',
  styleUrls: ['./covers.component.sass'],
})
export class CoversComponent {

  comics: Array<Comic> = [];
  frontCoverThumbnails = new Map<string, Observable<Thumbnail>>();
  backCoverThumbnails = new Map<string, Observable<Thumbnail>>();

  constructor (
    private comicsService: ComicsService,
    private thumbnailsService: ThumbnailsService,
    private toastController: ToastController,
  ) { }

  ionViewWillEnter () {
    this.list();
  }

  private list (): void {
    this.comicsService.listComicsWithoutErrors()
      .subscribe((data: Comic[]) => {
        this.comics = data;
        this.comics.forEach((comic: Comic) => this.updateThumbnails(comic));
      });
  }

  deleteFrontCover (comic: Comic) {
    this.frontCoverThumbnails.get(comic.id).subscribe(thumbail => {
      this.comicsService.deletePage(comic, thumbail.path).subscribe(() => {
        this.updateThumbnails(comic);
        this.showToast('Front cover deleted.');
      }, () => {
        this.showToast('Error whilte deleting front cover.');
      });
    });
  }

  deleteBackCover (comic: Comic) {
    this.backCoverThumbnails.get(comic.id).subscribe(thumbail => {
      this.comicsService.deletePage(comic, thumbail.path).subscribe(() => {
        this.updateThumbnails(comic);
        this.showToast(`Back cover of "${ comic.fileName }" deleted.`);
      }, () => {
        this.showToast(`Error while deleting back cover of "${ comic.fileName }".`);
      });
    });
  }

  private updateThumbnails (comic: Comic) {
    this.frontCoverThumbnails.set(comic.id, this.thumbnailsService.getFrontCover(comic.id));
    this.backCoverThumbnails.set(comic.id, this.thumbnailsService.getBackCover(comic.id));
  }

  private async showToast (message: string, duration: number = 4000) {
    const toast = await this.toastController.create({
      message,
      duration
    });
    toast.present();
  }
}
