import { of } from 'rxjs';

import { VolumesService } from '../app/volumes.service';

import { publisher1 as publisher } from './publisher.fixtures';
import { series1 as series } from './series.fixtures';
import { volume1 as volume } from './volume.fixtures';

export class VolumesServiceMocks {

  public static get volumesService(): jasmine.SpyObj<VolumesService> {
    return jasmine.createSpyObj('VolumesService', {
      listVolumesByPublisher: of([volume]),
      listVolumes: of([volume]),
      listSeries: of([series]),
      listPublishers: of([publisher])
    });
  }
}
