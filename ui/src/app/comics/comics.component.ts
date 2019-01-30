import { Component } from '@angular/core';
import { Subscription } from 'rxjs';

import { ComicsService } from './../comics.service';
import { Comic } from './../comic';
import { Publisher } from './../publisher';

@Component({
  selector: 'app-comics',
  templateUrl: './comics.component.html',
  styleUrls: ['./comics.component.sass']
})
export class ComicsComponent {
  total = 0;
  file: string;
  counter = 0;
  errors: any[] = [];

  publishers: Array<Publisher> = [];

  constructor (
    private comicsService: ComicsService
  ) {
    this.list();
  }

  private list () {
    this.comicsService.listVolumesByPublisher()
      .subscribe((data: Publisher[]) => {
        this.publishers = data;
      });
  }
}
