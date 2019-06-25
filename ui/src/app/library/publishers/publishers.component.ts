import { Component } from '@angular/core';

import { VolumesService } from '../../volumes.service';
import { Series } from '../../series';
import { Publisher } from '../../publisher';

@Component({
  selector: 'app-publishers',
  templateUrl: './publishers.component.html',
  styleUrls: ['./publishers.component.sass'],
})
export class PublishersComponent {

  private publishersData: Publisher[];
  publishers: Publisher[];

  constructor (
    private volumesService: VolumesService
  ) {
    this.list();
  }

  private list () {
    this.volumesService.listPublishers()
      .subscribe((data: Publisher[]) => {
        this.publishersData = data;
        this.publishers = this.publishersData;
      });
  }

  filter (value: string) {
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
}
