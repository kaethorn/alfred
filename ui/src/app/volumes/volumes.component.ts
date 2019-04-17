import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';

import { ComicsService } from './../comics.service';
import { VolumesService } from './../volumes.service';
import { Comic } from '../comic';

@Component({
  selector: 'app-volumes',
  templateUrl: './volumes.component.html',
  styleUrls: ['./volumes.component.sass']
})
export class VolumesComponent implements OnInit {

  private publisher: string;
  private series: string;
  private volume: string;

  comics: Array<Comic> = [];

  constructor(
    private sanitizer: DomSanitizer,
    private route: ActivatedRoute,
    private comicsService: ComicsService,
    private volumesService: VolumesService
    ) {
  }

  ngOnInit() {
    this.publisher = this.route.snapshot.params.publisher;
    this.series = this.route.snapshot.params.series;
    this.volume = this.route.snapshot.params.volume;

    this.list();
  }

  markAsRead (comic: Comic): void {
    this.comicsService.markAsRead(comic).subscribe((resultComic) => {
      this.replaceComic(resultComic);
    });
  }

  markAsUnread (comic: Comic): void {
    this.comicsService.markAsUnread(comic).subscribe((resultComic) => {
      this.replaceComic(resultComic);
    });
  }

  markAsReadUntil (comic: Comic): void {
    this.volumesService.markAllAsReadUntil(comic).subscribe(() => {
      this.list();
    });
  }

  thumbnail (comic: Comic): SafeUrl {
    return this.sanitizer.bypassSecurityTrustUrl(`data:image/jpeg;base64,${ comic.thumbnail }`);
  }

  private list (): void {
    this.comicsService.listByVolume(this.publisher, this.series, this.volume)
      .subscribe((data: Comic[]) => {
        this.comics = data;
      });
  }

  private replaceComic (comic: Comic): void {
    this.comics[this.comics.findIndex(c => c.id === comic.id)] = comic;
  }
}
