import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { VolumesService } from '../../volumes.service';
import { Series } from '../../series';

@Component({
  selector: 'app-series',
  templateUrl: './series.component.html',
  styleUrls: ['./series.component.sass']
})
export class SeriesComponent {

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
      .filter(series => series.series.match(value));
  }

  private list(publisher: string): void {
    this.volumesService.listSeries(publisher)
      .subscribe((data: Series[]) => {
        this.seriesData = data;
        this.series = this.seriesData;
      });
  }
}
