import { Component } from '@angular/core';
import { ToastController } from '@ionic/angular';
import { Observable } from 'rxjs';

import { Comic } from '../../comic';
import { ComicsService } from '../../comics.service';
import { Thumbnail } from '../../thumbnail';
import { ThumbnailsService } from '../../thumbnails.service';

@Component({
  selector: 'app-covers',
  templateUrl: './covers.component.html',
  styleUrls: ['./covers.component.sass']
})
export class CoversComponent {

  public comics: Array<Comic> = [];
  public frontCoverThumbnails = new Map<string, Observable<Thumbnail>>();
  public backCoverThumbnails = new Map<string, Observable<Thumbnail>>();

  constructor(
    private comicsService: ComicsService,
    private thumbnailsService: ThumbnailsService,
    private toastController: ToastController
  ) { }

  public ionViewWillEnter(): void {
    this.list();
  }

  public deleteFrontCover(comic: Comic): void {
    this.frontCoverThumbnails.get(comic.id).subscribe(thumbail => {
      this.comicsService.deletePage(comic, thumbail.path).subscribe(() => {
        this.updateThumbnails(comic);
        this.showToast('Front cover deleted.');
      }, () => {
        this.showToast('Error whilte deleting front cover.');
      });
    });
  }

  public deleteBackCover(comic: Comic): void {
    this.backCoverThumbnails.get(comic.id).subscribe(thumbail => {
      this.comicsService.deletePage(comic, thumbail.path).subscribe(() => {
        this.updateThumbnails(comic);
        this.showToast(`Back cover of "${ comic.fileName }" deleted.`);
      }, () => {
        this.showToast(`Error while deleting back cover of "${ comic.fileName }".`);
      });
    });
  }

  private updateThumbnails(comic: Comic): void {
    this.frontCoverThumbnails.set(comic.id, this.thumbnailsService.getFrontCover(comic.id));
    this.backCoverThumbnails.set(comic.id, this.thumbnailsService.getBackCover(comic.id));
  }

  private async showToast(message: string, duration = 4000): Promise<void> {
    const toast = await this.toastController.create({
      message,
      duration
    });
    toast.present();
  }

  private list(): void {
    this.comicsService.listComicsWithoutErrors()
      .subscribe((data: Comic[]) => {
        this.comics = data;
        this.comics.forEach((comic: Comic) => this.updateThumbnails(comic));
      });
  }
}
