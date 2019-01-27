import { Component } from '@angular/core';
import { Subscription } from 'rxjs';

import { ComicsService } from './../comics.service';
import { Comic } from './../comic';
import { Volume } from './../volume';

interface Error {
  message: string;
  date: Date;
}

@Component({
  selector: 'app-comics',
  templateUrl: './comics.component.html',
  styleUrls: ['./comics.component.sass']
})
export class ComicsComponent {
  total: number = 0;
  file: string;
  counter: number = 0;
  errors: any[] = [];

  comics: Array<Comic> = [];
  seriesList: Array<Volume> = [];

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

    scanProgress.addEventListener('error', (event: any) => {
      this.errors.push({ message: event.data, date: new Date().toISOString() });
    });

    scanProgress.addEventListener('done', () => {
      this.counter = 0;
      this.total = 0;
      this.list();
      scanProgress.close();
    });
  }

  private list () {
    this.comicsService.listVolumes()
      .subscribe((data: Volume[]) => {
        this.seriesList = data;
      });
  }
}
