import { Component } from '@angular/core';
import { ToastController, LoadingController } from '@ionic/angular';
import { Observable } from 'rxjs';

import { Comic } from '../../comic';
import { ComicsService } from '../../comics.service';
import { Thumbnail } from '../../thumbnail';
import { ThumbnailsService } from '../../thumbnails.service';

@Component({
  selector: 'app-covers',
  styleUrls: [ './covers.component.sass' ],
  templateUrl: './covers.component.html'
})
export class CoversComponent {

  public comics: Array<Comic> = [];
  public frontCoverThumbnails = new Map<string, Observable<Thumbnail>>();
  public backCoverThumbnails = new Map<string, Observable<Thumbnail>>();

  constructor(
    private comicsService: ComicsService,
    private thumbnailsService: ThumbnailsService,
    private toastController: ToastController,
    private loadingController: LoadingController
  ) { }

  public ionViewWillEnter(): void {
    this.list();
  }

  public deleteFrontCover(comic: Comic): void {
    this.frontCoverThumbnails.get(comic.id).subscribe(thumbail => {
      this.comicsService.deletePage(comic, thumbail.path).subscribe(() => {
        this.updateThumbnails(comic);
        this.showToast(`Front cover of "${ comic.fileName }" deleted.`);
      }, () => {
        this.showToast(`Error while deleting front cover of "${ comic.fileName }".`, 4000);
      });
    });
  }

  public deleteBackCover(comic: Comic): void {
    this.backCoverThumbnails.get(comic.id).subscribe(thumbail => {
      this.comicsService.deletePage(comic, thumbail.path).subscribe(() => {
        this.updateThumbnails(comic);
        this.showToast(`Back cover of "${ comic.fileName }" deleted.`);
      }, () => {
        this.showToast(`Error while deleting back cover of "${ comic.fileName }".`, 4000);
      });
    });
  }

  private updateThumbnails(comic: Comic): void {
    this.frontCoverThumbnails.set(comic.id, this.thumbnailsService.getFrontCover(comic.id));
    this.backCoverThumbnails.set(comic.id, this.thumbnailsService.getBackCover(comic.id));
  }

  private async showToast(message: string, duration = 3000): Promise<void> {
    const toast = await this.toastController.create({
      duration,
      message
    });
    toast.present();
  }

  private async presentLoading(): Promise<HTMLIonLoadingElement> {
    const loading = await this.loadingController.create({
      message: 'Loading covers...'
    });
    await loading.present();
    return loading;
  }

  private async list(): Promise<void> {
    const loading = await this.presentLoading();
    this.comicsService.listComicsWithoutErrors()
      .subscribe((data: Comic[]) => {
        loading.dismiss();
        this.comics = data;
        this.comics.forEach((comic: Comic) => this.updateThumbnails(comic));
      }, () => loading.dismiss());
  }
}
