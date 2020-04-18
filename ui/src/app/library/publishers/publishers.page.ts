import { Component } from '@angular/core';

import { Publisher } from '../../publisher';
import { Series } from '../../series';
import { VolumesService } from '../../volumes.service';

@Component({
  selector: 'app-publishers',
  styleUrls: [ './publishers.page.sass' ],
  templateUrl: './publishers.page.html'
})
export class PublishersPage {

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
