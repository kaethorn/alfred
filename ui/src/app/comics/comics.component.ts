import { Component } from '@angular/core';
import { ComicsService } from './comics.service';
import { Comic } from './comic';

@Component({
  selector: 'app-comics',
  templateUrl: './comics.component.html',
  styleUrls: ['./comics.component.css']
})
export class ComicsComponent {
  constructor (
    private comicsService: ComicsService
  ) {
    this.list();
  }

  comics: Array<Comic> = [];
  scanStatus: boolean = null;

  scan () {
    this.comicsService.scan()
      .subscribe((data: Array<Comic>, foo?, bar?) => {
        this.scanStatus = foo;
      });
  }

  list () {
    this.comicsService.list()
      .subscribe((data: any) => {
        this.comics = data._embedded.comics as Comic[];
      });
  }
}
