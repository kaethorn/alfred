import { Component } from '@angular/core';

import { Publisher } from '../../publisher';
import { Series } from '../../series';
import { VolumesService } from '../../volumes.service';

@Component({
  selector: 'app-publishers',
  styleUrls: [ './publishers.component.sass' ],
  templateUrl: './publishers.component.html'
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
        const series = publisher.series.filter((serie: Series) => serie.name.match(value));
        if (series.length) {
          result.push({
            name: publisher.name,
            series,
            seriesCount: publisher.seriesCount
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
