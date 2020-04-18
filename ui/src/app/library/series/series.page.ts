import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { Series } from '../../series';
import { VolumesService } from '../../volumes.service';

@Component({
  selector: 'app-series',
  styleUrls: [ './series.page.sass' ],
  templateUrl: './series.page.html'
})
export class SeriesPage {

  public series: Series[];
  public publisher = '';
  private seriesData: Series[];

  constructor(
    private route: ActivatedRoute,
    private volumesService: VolumesService
  ) { }

  public ionViewDidEnter(): void {
    this.publisher = this.route.snapshot.params.publisher;
    this.list(this.publisher);
  }

  public filter(value: string): void {
    this.series = this.seriesData
      .filter(series => series.name.match(value));
  }

  private list(publisher: string): void {
    this.volumesService.listSeries(publisher)
      .subscribe((data: Series[]) => {
        this.seriesData = data;
        this.series = this.seriesData;
      });
  }
}
