import { Component, Input, OnInit } from '@angular/core';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';

import { ComicsService } from './../../comics.service';
import { Comic } from '../../comic';
import { Volume } from '../../publisher';

@Component({
  selector: 'app-volume',
  templateUrl: './volume.component.html',
  styleUrls: ['./volume.component.sass']
})
export class VolumeComponent implements OnInit {

  @Input() volume: Volume;
  @Input() series: string;
  @Input() publisher: string;

  comics: Array<Comic> = [];

  constructor (
    private comicsService: ComicsService,
    private sanitizer: DomSanitizer
  ) {
  }

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

  get volumeThumbnail(): SafeUrl {
    return this.sanitizer.bypassSecurityTrustUrl(`data:image/jpeg;base64,${ this.volume.thumbnail }`);
  }

  comicThumbnail (comic: Comic): SafeUrl {
    return this.sanitizer.bypassSecurityTrustUrl(`data:image/jpeg;base64,${ comic.thumbnail }`);
  }
}
