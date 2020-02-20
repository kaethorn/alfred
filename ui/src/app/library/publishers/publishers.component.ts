import { Component } from '@angular/core';

import { VolumesService } from '../../volumes.service';
import { Series } from '../../series';
import { Publisher } from '../../publisher';

@Component({
  selector: 'app-publishers',
  templateUrl: './publishers.component.html',
  styleUrls: ['./publishers.component.sass']
})
export class PublishersComponent {

  public publishers: Publisher[];
  private publishersData: Publisher[];

  constructor(
    private volumesService: VolumesService
  ) {
    this.list();
  }

  public filter(value: string): void {
    this.publishers = this.publishersData
      .reduce((result: Publisher[], publisher: Publisher): Publisher[] => {
        const series = publisher.series.filter((serie: Series) => serie.series.match(value));
        if (series.length) {
          result.push({
            publisher: publisher.publisher,
            seriesCount: publisher.seriesCount,
            series
          });
        }
        return result;
      }, []);
  }

  private list(): void {
    this.volumesService.listPublishers()
      .subscribe((data: Publisher[]) => {
        this.publishersData = data;
        this.publishers = this.publishersData;
      });
  }
}
