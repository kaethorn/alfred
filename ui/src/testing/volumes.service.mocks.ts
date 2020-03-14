import { of } from 'rxjs';

import { VolumesService } from '../app/volumes.service';

import { PublisherFixtures } from './publisher.fixtures';
import { SeriesFixtures } from './series.fixtures';
import { VolumeFixtures } from './volume.fixtures';

export class VolumesServiceMocks {

  public static get volumesService(): jasmine.SpyObj<VolumesService> {
    return jasmine.createSpyObj('VolumesService', {
      listVolumesByPublisher: of([VolumeFixtures.volume]),
      listVolumes: of([VolumeFixtures.volume]),
      listSeries: of([SeriesFixtures.series]),
      listPublishers: of([PublisherFixtures.publisher]),
      markAsRead: of(null),
      markAsUnread: of(null),
      markAllAsReadUntil: of(null)
    });
  }
}
