import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { VolumesService } from '../../volumes.service';
import { Series } from '../../series';

@Component({
  selector: 'app-series',
  templateUrl: './series.component.html',
  styleUrls: ['./series.component.sass'],
})
export class SeriesComponent {

  private seriesData: Series[];
  series: Series[];
  publisher = '';

  constructor (
    private route: ActivatedRoute,
    private volumesService: VolumesService
  ) { }

  ionViewDidEnter () {
    this.publisher = this.route.snapshot.params.publisher;
    this.list(this.publisher);
  }

  private list (publisher: string) {
    this.volumesService.listSeries(publisher)
      .subscribe((data: Series[]) => {
        this.seriesData = data;
        this.series = this.seriesData;
      });
  }

  filter (value: string) {
    this.series = this.seriesData
      .filter(series => series.series.match(value));
  }
}
