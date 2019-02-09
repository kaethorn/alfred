import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { ComicsService } from './../comics.service';
import { Publisher } from './../publisher';

@Component({
  selector: 'app-library',
  templateUrl: './library.component.html',
  styleUrls: ['./library.component.sass']
})
export class LibraryComponent implements OnInit {

  publishers: Array<Publisher> = [];
  currentPublisher: string;
  currentSeries: string;

  constructor (
    private route: ActivatedRoute,
    private router: Router,
    private comicsService: ComicsService
  ) {
    this.list();
  }

  ngOnInit() {
    this.currentPublisher = this.route.snapshot.params.publisher;
    this.currentSeries = this.route.snapshot.params.series;
  }

  openPublisher (publisher: string) {
    this.currentPublisher = publisher;
    this.router.navigate(['/library', this.currentPublisher]);
  }

  openSeries (series: string) {
    this.currentSeries = series;
    this.router.navigate(['/library', this.currentPublisher, this.currentSeries]);
  }

  onScanned (scanned) {
    if (scanned) {
      this.list();
    }
  }

  private list () {
    this.comicsService.listVolumesByPublisher()
      .subscribe((data: Publisher[]) => {
        this.publishers = data;
      });
  }
}
