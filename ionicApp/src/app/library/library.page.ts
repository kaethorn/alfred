import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { VolumesService } from './../volumes.service';
import { Publisher } from './../publisher';

@Component({
  selector: 'app-library',
  templateUrl: './library.page.html',
  styleUrls: ['./library.page.sass']
})
export class LibraryPage implements OnInit {

  publishersData: Array<Publisher> = [];
  publishers: Array<Publisher> = [];
  currentSeries: string;

  constructor (
    private route: ActivatedRoute,
    private router: Router,
    private volumesService: VolumesService
  ) {
    this.list();
  }

  ngOnInit() {
    this.currentSeries = this.route.snapshot.queryParams.series;
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
    this.publishers = this.publishersData
      .filter(publisher => publisher.series.filter(series => series.series.match(value)).length)
      .map(publisher => ({
        ...publisher,
        series: publisher.series.filter(series => series.series.match(value))
      }));
  }
}
