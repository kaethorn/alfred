import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { VolumesService } from './../volumes.service';
import { Publisher } from './../publisher';

@Component({
  selector: 'app-library',
  templateUrl: './library.component.html',
  styleUrls: ['./library.component.sass']
})
export class LibraryComponent implements OnInit {

  publishersData: Array<Publisher> = [];
  publishers: Array<Publisher> = [];
  currentPublisher: string;
  currentSeries: string;

  constructor (
    private route: ActivatedRoute,
    private router: Router,
    private volumesService: VolumesService
  ) {
    this.list();
  }

  ngOnInit() {
    this.currentPublisher = this.route.snapshot.queryParams.publisher;
    this.currentSeries = this.route.snapshot.queryParams.series;
  }

  openPublisher (publisher: string) {
    this.currentPublisher = publisher;
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { publisher: publisher },
      queryParamsHandling: 'merge'
    });
  }

  openSeries (series: string) {
    this.currentSeries = series;
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { series: series },
      queryParamsHandling: 'merge'
    });
  }

  list () {
    this.volumesService.listVolumesByPublisher()
      .subscribe((data: Publisher[]) => {
        this.publishersData = data;
        this.publishers = this.publishersData;
      });
  }

  filter (value: string) {
    this.publishers = this.publishersData.filter(publisher => publisher.publisher.match(value));
  }
}
