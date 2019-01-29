import { Component, Input, OnInit } from '@angular/core';

import { ComicsService } from './../../comics.service';
import { Comic } from '../../comic';

@Component({
  selector: 'app-volume',
  templateUrl: './volume.component.html',
  styleUrls: ['./volume.component.sass']
})
export class VolumeComponent implements OnInit {

  @Input() volume: string;
  @Input() series: string;
  @Input() publisher: string;

  comics: Array<Comic> = [];

  constructor (
    private comicsService: ComicsService
  ) {}

  ngOnInit() {
  }

  toggleIssues (publisher: string, series: string, volume: string) {
    if (this.comics.length) {
      this.comics = [];
    } else {
      this.comicsService.listByVolume(publisher, series, volume)
        .subscribe((data: Comic[]) => {
          this.comics = data;
        });
    }
  }
}
