import { of } from 'rxjs';

import { volume1 as volume } from './volume.fixtures';
import { series1 as series } from './series.fixtures';
import { publisher1 as publisher } from './publisher.fixtures';

const volumesService = jasmine.createSpyObj('VolumesService', [
  'listVolumesByPublisher',
  'listVolumes',
  'listSeries',
  'listPublishers'
]);

volumesService.listVolumesByPublisher.and.returnValue( of([volume]) );
volumesService.listVolumes.and.returnValue( of([volume]) );
volumesService.listSeries.and.returnValue( of([series]) );
volumesService.listPublishers.and.returnValue( of([publisher]) );

export { volumesService as VolumesServiceMocks };
