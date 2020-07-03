import { Component } from '@angular/core';
import { ToastController, LoadingController } from '@ionic/angular';

import { Comic, ScannerIssue } from '../../comic';
import { ComicsService } from '../../comics.service';

@Component({
  selector: 'app-queue',
  styleUrls: [ './queue.page.sass' ],
  templateUrl: './queue.page.html'
})
export class QueuePage {

  public comics: Array<Comic> = [];
  public issueSeverityToColor = { ERROR: 'danger', INFO: 'secondary', WARNING: 'warning' };

  constructor(
    private comicsService: ComicsService,
    private toastController: ToastController,
    private loadingController: LoadingController
  ) { }

  public ionViewWillEnter(): void {
    this.list();
  }

  public fix(comic: Comic, error: ScannerIssue): void {
    this.comicsService.fixIssue(comic, error).subscribe(() => {
      this.list();
      this.showToast(`Flattened comic archive "${ comic.fileName }".`);
    }, () => {
      this.showToast(`Error while flattening comic archive "${ comic.fileName }".`, 4000);
    });
  }

  private async presentLoading(): Promise<HTMLIonLoadingElement> {
    const loading = await this.loadingController.create({
      message: 'Loading queue...'
    });
    await loading.present();
    return loading;
  }

  private async list(): Promise<void> {
    const loading = await this.presentLoading();
    this.comicsService.listComicsWithErrors()
      .subscribe((data: Comic[]) => {
        loading.dismiss();
        this.comics = data;
      }, () => loading.dismiss());
  }

  private async showToast(message: string, duration = 3000): Promise<void> {
    const toast = await this.toastController.create({
      duration,
      message
    });
    toast.present();
  }
}
