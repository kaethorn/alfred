import { Component } from '@angular/core';
import { Comic } from '../comic';
import { ComicsService } from '../comics.service';

@Component({
  selector: 'app-queue',
  templateUrl: './queue.page.html',
  styleUrls: ['./queue.page.sass'],
})
export class QueuePage {

  comics: Array<Comic> = [];

  constructor(
    private comicsService: ComicsService,
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
}
