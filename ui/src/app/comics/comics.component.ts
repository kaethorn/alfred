import { Component } from '@angular/core';
import { Subscription } from 'rxjs';

import { ComicsService } from './../comics.service';
import { Comic } from './../comic';

@Component({
  selector: 'app-comics',
  templateUrl: './comics.component.html',
  styleUrls: ['./comics.component.css']
})
export class ComicsComponent {
  total: number = 0;
  file: string;
  counter: number = 0;
  comics: Array<Comic> = [];

  constructor (
    private comicsService: ComicsService
  ) {
    this.list();
  }

  scan () {
    const scanProgress = new EventSource('/api/scan-progress');

    scanProgress.addEventListener('total', (event: any) => {
      this.total = this.total || event.data;
    });

    scanProgress.addEventListener('current-file', (event: any) => {
      this.file = event.data;
      this.counter += 1;
    });

    scanProgress.addEventListener('done', () => {
      this.counter = 0;
      this.total = 0;
      this.list();
      scanProgress.close();
    });

    this.comicsService.scan().subscribe(() => {
    });
  }

  private list () {
    this.comicsService.list()
      .subscribe((data: Comic[]) => {
        this.comics = data;
      });
  }
}
