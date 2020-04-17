import { Component } from '@angular/core';
import { ToastController } from '@ionic/angular';

import { Comic, ScannerIssue } from '../../comic';
import { ComicsService } from '../../comics.service';

@Component({
  selector: 'app-queue',
  styleUrls: [ './queue.component.sass' ],
  templateUrl: './queue.component.html'
})
export class QueueComponent {

  public comics: Array<Comic> = [];
  public issueSeverityToColor = { ERROR: 'danger', INFO: 'secondary', WARNING: 'warning' };

  constructor(
    private comicsService: ComicsService,
    private toastController: ToastController
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

  private list(): void {
    this.comicsService.listComicsWithErrors()
      .subscribe((data: Comic[]) => {
        this.comics = data;
      });
  }

  private async showToast(message: string, duration = 3000): Promise<void> {
    const toast = await this.toastController.create({
      duration,
      message
    });
    toast.present();
  }
}
