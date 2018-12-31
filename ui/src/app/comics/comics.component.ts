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
  ) {}

  comics = [];

  scan () {
    this.comicsService.getComics()
      .subscribe((data: Array<Comic>) => this.comics = data);
  }
}
