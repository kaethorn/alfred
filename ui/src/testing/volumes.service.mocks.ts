import { of } from 'rxjs';

import { VolumesService } from '../app/volumes.service';

import { PublisherFixtures } from './publisher.fixtures';
import { SeriesFixtures } from './series.fixtures';
import { VolumeFixtures } from './volume.fixtures';

export class VolumesServiceMocks {

  public static get volumesService(): jasmine.SpyObj<VolumesService> {
    return jasmine.createSpyObj('VolumesService', {
      listPublishers: of(PublisherFixtures.publishers),
      listSeries: of(SeriesFixtures.series),
      listVolumes: of(VolumeFixtures.volumes),
      listVolumesByPublisher: of([ VolumeFixtures.volume ]),
      markAllAsReadUntil: of(null),
      markAsRead: of(null),
      markAsUnread: of(null)
    });
  }
}
