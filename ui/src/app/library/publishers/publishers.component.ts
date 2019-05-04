import { Component } from '@angular/core';

import { VolumesService } from '../../volumes.service';
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
      .filter(publisher => publisher.publisher.match(value));
  }
}
