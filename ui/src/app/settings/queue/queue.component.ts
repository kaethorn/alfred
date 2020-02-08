import { Component } from '@angular/core';
import { ToastController } from '@ionic/angular';

import { Comic } from '../../comic';
import { ComicsService } from '../../comics.service';

@Component({
  selector: 'app-queue',
  templateUrl: './queue.component.html',
  styleUrls: ['./queue.component.sass'],
})
export class QueueComponent {

  comics: Array<Comic> = [];
  issueSeverityToColor = { ERROR: 'danger', WARNING: 'warning', INFO: 'secondary' };

  constructor (
    private comicsService: ComicsService,
    private toastController: ToastController,
  ) { }

  ionViewWillEnter () {
    this.list();
  }

  private list (): void {
    this.comicsService.listComicsWithErrors()
      .subscribe((data: Comic[]) => {
        this.comics = data;
      });
  }

  ignore (comic: Comic): void {
    delete comic.errors;
    this.comicsService.update(comic).subscribe(() => {
      this.showToast('Comic errors ignored.');
      this.list();
    }, () => {
      this.showToast('Error while ignoring comic errors.');
    });
  }

  private async showToast (message: string, duration: number = 2000) {
    const toast = await this.toastController.create({
      message,
      duration
    });
    toast.present();
  }
}
