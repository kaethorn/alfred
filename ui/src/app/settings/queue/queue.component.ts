import { Component } from '@angular/core';
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
